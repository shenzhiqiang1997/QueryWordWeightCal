### ˹̹���ִ���
* �ִ���<br>
  ��˹̹���ִ�����LexicalizedParser�������Ϊ�ִʵĺ����� �����parser���ԶԾ��ӽ��зִ��빹���﷨��
* �ִ�ģ��<br>
  ˹̹���ִ����ṩ�����ģ��<br> ��ͬģ����Բ�ͬ���ԺͲ�ͬ�龳����ʹ��<br> ���÷ִ������зִ�ʱ ��Ҫָ��ʹ�õķִ�ģ��
* ���طִ�ģ��<br>
  ͨ����̬����LexicalizedParser.loadModel(model)�����طִ�ģ��<br>�ڼ��سɹ���᷵��һ���ִ���LexicalizedParser����parser<br>
* �ִ�<br>
  ͨ��parser.parse(sentence)�Ծ��ӽ��зִ�<br>
  �ִʳɹ��᷵���﷨������
* �﷨��<br>
  ��һ�����ӷִʺ󽫵õ�Tree����root<br> �ö���������﷨���ĸ�
* ���ʽڵ�<br>
  ����ͨ��root.getLeaves()��ȡ���﷨��������Ҷ�ӽڵ�<br> 
  ���þ����е����е��ʽڵ�
* ������ϵ<br>
  1. ͨ��parser.treebankLanguagePack()�õ��﷨�������԰�TreebankLanguagePack����tlp
  2. ͨ��tlp.grammaticalStructureFactory()�õ��﷨�ṹ����GrammaticalStructureFactory����gsf
  3. ͨ��gsf.newGrammaticalStructure(root)�õ���Ϊroot���﷨�����﷨�ṹGrammaticalStructure����gs
  4. ͨ��gs.typedDependenciesCCprocessed()�õ����﷨�ṹ�е�ÿ��������ϵ����dependency
  5. ͨ��dependency.reln()�õ��﷨��ϵGrammaticalRelation����gr
  6. ͨ��dependency.dep() dependency.dep()<br> �õ���������ϵ���������ʶ���<br> �����﷨��ϵ���������ʶ��� 
* �����﷨��<br>
Ϊ��ʵ�ּ��� ������﷨�����б����������﷨�����ƶ�<br>
���������ַ���:
    * root.preOrderNodeList()����root.postOrderNodeList()<br>
    ����ֱ�ӻ�ð�ǰ���������˳�����еĽڵ��б�
    * ����ͨ��root.children()��ȡ���ӽڵ�node����<br>
    ����node.children()����node.parent()�����ƶ�<br>
    ���ַ�����Ϊ���
* ʹ�÷ִ���������<br>
    ->ѡ���ִ�ģ��model<br>
    ->���طִ�ģ��model�����ִ�������parser<br>
    ->��parser��Ŀ����ӽ��зִʵõ��﷨����root<br>
    ->��ȡ�﷨������Ҷ�ڵ㼴���ʽڵ�<br>
    ->��ȡ�﷨����Ӧ������������ϵ<br>
    ->�����﷨�������﷨���Ͻ����ƶ�����ģ�ͽ��м���

### ģ��
stanford-parser-3.8.0-models.jar<br>
edu.standford.nlp.models.lexparser��<br>
����Щ�ִ�ģ��<br>
![image](https://note.youdao.com/yws/public/resource/708ad1b1ee41c2b335e0cac5c8d83d03/xmlnote/8214222B34DE4C9E8A7E6F5DDD018B7E/2830)<br>
��Ҫ��עÿ���������׺<br>
���·�Ϊ��������:
1. Factored
    * �ŵ�<br>
        ͨ��
    * ȱ��<br>
        �����Ǽ��ػ��Ƿִ��ٶȶ�����
    * ��ģ�ͼ�����������ĵĽ���������ĵĴʵ�Ȩ��ƫС �����ʵ�Ȩ��ƫ�� �����Ĵʵ�Ȩ����Ȼռ���
2. PCFG
    * �ŵ�<br>
        ��С ���غͷִʵ��ٶȽϿ� 
    * ȱ��<br>
        ���÷�Χ��խ
    * ��ģ�ͼ����������ĵĽ������һ��
3. RNN
    * ��ģ�ͼ��غͷִ��ٶ���ǰ����֮��
    * ��ģ�ͼ����������Ľ����ȫ��ͬ
* ����ʹ��PCFGģ��
