package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import model.Relation;
import model.Word;

public class Test {
	public static void main(String[] args) {
		HashMap<String,Tree> wordNodes=new HashMap<>();
		List<Relation> relations=new ArrayList<>();
		HashMap<String, Double> wordWeights=new HashMap<>();
		
		// Ҫ�ִʵľ���
		String sentence = "blue trouser with pockets";
		// �ִʵ�ģʽ
		String model = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

		// ��ȡ�ִ�������
		LexicalizedParser parser = LexicalizedParser.loadModel(model);
		// ͨ���ִ����ִ� �õ��﷨��
		Tree root = parser.parse(sentence);
		
		// ����Ҳ����Ϊһ������ ���ھ�����ʵ�ʲ����� 
		// �ڼ���d1 d2 ��d ʱ���
		// �ڼ���R(����) ʱ��Ҫ�Ƴ�
		wordNodes.put("ROOT", root);
		
		// ����﷨��
		System.out.println("tree:" + root);
		System.out.println();

		// �õ�����ÿ��Ҷ�� ��ÿ�����ʶ�Ӧ�Ľڵ�
		List<Tree> leaves = root.getLeaves();
		for (Tree node : leaves) {
			wordNodes.put(node.value(), node);
		}

		// �ӷִ���������Ӧģʽ�õ��﷨�������԰�
		TreebankLanguagePack tlp = parser.treebankLanguagePack();
		// �����԰��õ��﷨�ṹ��������
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// �����﷨�����󹹽��﷨�ṹ����
		GrammaticalStructure gs = gsf.newGrammaticalStructure(root);

		// ���﷨�ṹ�õ�����dependency
		List<TypedDependency> dependencies = gs.typedDependenciesCCprocessed();
		
		System.out.println("dependencies:");
		//����dependency
		for (TypedDependency dependency : dependencies) {
			System.out.println(dependency);
			
			String relationName=dependency.reln().toString();
			Tree leaf1=wordNodes.get(dependency.dep().value());
			Tree leaf2=wordNodes.get(dependency.gov().value());
			
			//����dependency���Լ�Ҷ�ڵ㵥����������ϵ
			relations.add(genreateRelation(relationName, leaf1, leaf2, root));
		}
		System.out.println();
		
		
		//����ÿ����ϵ��R
		calculateR(relations);
		
		//�õ������Ĺ�ϵ
		System.out.println("relations:");
		for (Relation relation : relations) {
			System.out.println(relation);
		}
		System.out.println();
		
		
		//�������й�ϵ������Ȩ��
		double totalImportance=getTotalImportance(relations);
		System.out.println("total importance:");
		System.out.println(totalImportance);
		System.out.println();
		
		//ͳ�Ƶ��ʸ���
		int t=leaves.size();
		//����ÿ�����ʵ�Ȩ��
		for (Tree wordLeaf : leaves) {
			String word=wordLeaf.value();
			double weight=getWordWeight(word, relations, totalImportance, t);
			wordWeights.put(word, weight);
		}
		
		System.out.println("word weights:");
		Iterator<Entry<String,Double>> iterator=wordWeights.entrySet().iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		
	}

	/**
	 * @param relationName ��ϵ����
	 * @param leaf1 Ҷ�ӵ���1
	 * @param leaf2 Ҷ�ӵ���2
	 * @param root  �﷨����
	 * @return
	 */
	public static Relation genreateRelation(String relationName,Tree leaf1, Tree leaf2, Tree root) {
		int d1 = 0;
		int d2 = 0;
		
		//�������1 ����root��ϵ 
		//Ӧ�ðѸ�root���﷨��ϵ�Ĵʵ�di����Ϊ0 
		//root��di����Ϊ������֮���﷨��ϵ�Ĵ�֮��ľ���
		if(leaf1==root||leaf2==root) {
			Tree leaf=null;
			boolean flagForLeaf1=false;
			int d=0;
			
			flagForLeaf1=(leaf1!=root);
			if(flagForLeaf1)
				leaf=leaf1;
			else
				leaf=leaf2;
			
			for(Tree node=leaf.parent(root);node!=null;node=node.parent(root)) {
				d++;
			}
			
			if(flagForLeaf1) {
				d1=0;
				d2=d;
			}else {
				d2=0;
				d1=d;
			}
			
		}else {
			//������� 
			//��leaf1һ·����Ѱ�� ֱ���ҵ���һ���������� 
			//d1 �� d2 ��Ϊ���������ȵľ���
			B: for (Tree node1 = leaf1.parent(root); node1 != null; node1 = node1.parent(root)) {
				d1++;
				for (Tree node2 = leaf2.parent(root); node2 != null; node2 = node2.parent(root)) {
					d2++;
					if (node1 == node2) {
						break B;
					}
				}
				d2 = 0;
			}
		}
		
		//����Ҷ�ڵ��ֵ���䵽�������ȵľ��빹������
		Word word1=new Word(leaf1.value(), d1);
		Word word2=new Word(leaf2.value(), d2);
		
		
		//����ģ����d=d1+d2
		//���� d ��ϵ����  ���﷨��ϵ���������� ������ϵ
		Relation relation = new Relation(relationName,word1,word2,d1+d2);
		
		return relation;
	}
	
	//�õ����й�ϵ��R
	//ע������root��ϵҪ���ȡr0
	//ע������case��ϵҪɾ��
	/**
	 * @param relations ���й�ϵ
	 */
	public static void calculateR(List<Relation> relations) {
		int maxD=relations.get(0).getD();
		int minD=relations.get(0).getD();
		
		int r0=0;
		
		//�������ȡr0
		Random random=new Random();
		
		//�ҵ�max(d)��min(d)
		for (Relation relation : relations) {
			if(relation.getD()>maxD)
				maxD=relation.getD();
			if(relation.getD()<minD)
				minD=relation.getD();
		}
		
		//�õ�r0
		r0=random.nextInt(maxD-minD)+minD;
		
		
		//�������й�ϵ �õ��ù�ϵ��R
		for (Relation relation : relations) {
			if(relation.getName()=="root") {
				//�����ϵΪroot �� R=1/ln(r0)
				relation.setR(1/Math.log(r0));
			}else if(relation.getName()=="case"){
				//�����ϵΪcase ��Ҫɾ�������  RΪ0
				relation.setR(0);
			}else{
				//������� ��R=1/ln(d)
				relation.setR(1/Math.log(relation.getD()));
			}
		}
	
	}
	
	//����ģ�͹�ʽ��������Ҫ�� ��(1,t)(1+��R)
	public static double getTotalImportance(List<Relation> relations) {
		double totalImportance=0;
		
		//�������й�ϵ �õ�����Ҫ��
		for (Relation relation : relations) {
			//��ÿ����ϵ��1+R����
			totalImportance=totalImportance+1+relation.getR();
		}
		
		return totalImportance;
	}
	
	
	
	//����ģ�͹�ʽ����һ�����ʵ�Ȩ��
	/**
	 * @param word Ҫ����ĵ����ַ���
	 * @param relations ���й�ϵ
	 * @param totalImportance ����Ҫ��
	 * @param t ���ʸ���
	 * @return weight �õ���Ȩ��
	 */
	public static double getWordWeight(String word,List<Relation> relations,double totalImportance,int t) {
		//�ȸ���1+��R����õ�һ�����ʵ���Ҫ��
		double wordImportance=1.0;
		
		double weight=0;
		
		for (Relation relation : relations) {
			//ȡ��һ����ϵ�е���������
			Word word1=relation.getWord1();
			Word word2=relation.getWord2();
			
			//����õ����ڸù�ϵ�вż��ϸõ����ڸù�ϵ�е�R
			if(word1.getName().equals(word)||word2.getName().equals(word)) {
				
				Word calWord;
				//�ҵ��ù�ϵ�е��������
				if(word1.getName().equals(word)) {
					calWord=word1;
				}else {
					calWord=word2;
				}
				//����[(d-d1)/d]*R�õ��õ����ڸù�ϵ�е�R
				wordImportance+=((double)(relation.getD()-calWord.getDi())/relation.getD())*relation.getR();
			}
		}
		
		//���㵥�����յ�Ȩ�� ����ģ�͹�ʽ[(1+��R)*t/��(1,t)(1+��R)]����
		weight=wordImportance*t/totalImportance;
		
		return weight;
	}
}
