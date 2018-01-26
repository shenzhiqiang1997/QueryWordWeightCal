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
		
		// 要分词的句子
		String sentence = "blue trouser with pockets";
		// 分词的模式
		String model = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

		// 获取分词器对象
		LexicalizedParser parser = LexicalizedParser.loadModel(model);
		// 通过分词器分词 得到语法树
		Tree root = parser.parse(sentence);
		
		// 树根也可视为一个单词 但在句子中实际不存在 
		// 在计算d1 d2 和d 时添加
		// 在计算R(单词) 时需要移除
		wordNodes.put("ROOT", root);
		
		// 输出语法树
		System.out.println("tree:" + root);
		System.out.println();

		// 得到树的每个叶子 即每个单词对应的节点
		List<Tree> leaves = root.getLeaves();
		for (Tree node : leaves) {
			wordNodes.put(node.value(), node);
		}

		// 从分词器根据相应模式得到语法树的语言包
		TreebankLanguagePack tlp = parser.treebankLanguagePack();
		// 从语言包得到语法结构工厂对象
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// 根据语法树对象构建语法结构对象
		GrammaticalStructure gs = gsf.newGrammaticalStructure(root);

		// 从语法结构得到所有dependency
		List<TypedDependency> dependencies = gs.typedDependenciesCCprocessed();
		
		System.out.println("dependencies:");
		//遍历dependency
		for (TypedDependency dependency : dependencies) {
			System.out.println(dependency);
			
			String relationName=dependency.reln().toString();
			Tree leaf1=wordNodes.get(dependency.dep().value());
			Tree leaf2=wordNodes.get(dependency.gov().value());
			
			//根据dependency名以及叶节点单词来构建关系
			relations.add(genreateRelation(relationName, leaf1, leaf2, root));
		}
		System.out.println();
		
		
		//计算每个关系的R
		calculateR(relations);
		
		//得到完整的关系
		System.out.println("relations:");
		for (Relation relation : relations) {
			System.out.println(relation);
		}
		System.out.println();
		
		
		//根据所有关系计算总权重
		double totalImportance=getTotalImportance(relations);
		System.out.println("total importance:");
		System.out.println(totalImportance);
		System.out.println();
		
		//统计单词个数
		int t=leaves.size();
		//计算每个单词的权重
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
	 * @param relationName 关系名称
	 * @param leaf1 叶子单词1
	 * @param leaf2 叶子单词2
	 * @param root  语法树根
	 * @return
	 */
	public static Relation genreateRelation(String relationName,Tree leaf1, Tree leaf2, Tree root) {
		int d1 = 0;
		int d2 = 0;
		
		//特殊情况1 对于root关系 
		//应该把跟root有语法关系的词的di设置为0 
		//root的di设置为它到与之有语法关系的词之间的距离
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
			//正常情况 
			//从leaf1一路向上寻找 直到找到第一个公共祖先 
			//d1 和 d2 即为到公共祖先的距离
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
		
		//根据叶节点的值和其到公共祖先的距离构建单词
		Word word1=new Word(leaf1.value(), d1);
		Word word2=new Word(leaf2.value(), d2);
		
		
		//根据模型有d=d1+d2
		//根据 d 关系名称  有语法关系的两个单词 构建关系
		Relation relation = new Relation(relationName,word1,word2,d1+d2);
		
		return relation;
	}
	
	//得到所有关系的R
	//注意其中root关系要随机取r0
	//注意其中case关系要删除
	/**
	 * @param relations 所有关系
	 */
	public static void calculateR(List<Relation> relations) {
		int maxD=relations.get(0).getD();
		int minD=relations.get(0).getD();
		
		int r0=0;
		
		//用于随机取r0
		Random random=new Random();
		
		//找到max(d)与min(d)
		for (Relation relation : relations) {
			if(relation.getD()>maxD)
				maxD=relation.getD();
			if(relation.getD()<minD)
				minD=relation.getD();
		}
		
		//得到r0
		r0=random.nextInt(maxD-minD)+minD;
		
		
		//遍历所有关系 得到该关系的R
		for (Relation relation : relations) {
			if(relation.getName()=="root") {
				//如果关系为root 则 R=1/ln(r0)
				relation.setR(1/Math.log(r0));
			}else if(relation.getName()=="case"){
				//如果关系为case 是要删除的情况  R为0
				relation.setR(0);
			}else{
				//正常情况 则R=1/ln(d)
				relation.setR(1/Math.log(relation.getD()));
			}
		}
	
	}
	
	//根据模型公式计算总重要性 ∑(1,t)(1+∑R)
	public static double getTotalImportance(List<Relation> relations) {
		double totalImportance=0;
		
		//遍历所有关系 得到总重要性
		for (Relation relation : relations) {
			//即每个关系的1+R叠加
			totalImportance=totalImportance+1+relation.getR();
		}
		
		return totalImportance;
	}
	
	
	
	//根据模型公式计算一个单词的权重
	/**
	 * @param word 要计算的单词字符串
	 * @param relations 所有关系
	 * @param totalImportance 总重要性
	 * @param t 单词个数
	 * @return weight 该单词权重
	 */
	public static double getWordWeight(String word,List<Relation> relations,double totalImportance,int t) {
		//先根据1+∑R计算得到一个单词的重要性
		double wordImportance=1.0;
		
		double weight=0;
		
		for (Relation relation : relations) {
			//取出一个关系中的两个单词
			Word word1=relation.getWord1();
			Word word2=relation.getWord2();
			
			//如果该单词在该关系中才加上该单词在该关系中的R
			if(word1.getName().equals(word)||word2.getName().equals(word)) {
				
				Word calWord;
				//找到该关系中的这个单词
				if(word1.getName().equals(word)) {
					calWord=word1;
				}else {
					calWord=word2;
				}
				//根据[(d-d1)/d]*R得到该单词在该关系中的R
				wordImportance+=((double)(relation.getD()-calWord.getDi())/relation.getD())*relation.getR();
			}
		}
		
		//计算单词最终的权重 根据模型公式[(1+∑R)*t/∑(1,t)(1+∑R)]计算
		weight=wordImportance*t/totalImportance;
		
		return weight;
	}
}
