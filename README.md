### 斯坦福分词器
* 分词器<br>
  在斯坦福分词器中LexicalizedParser这个类作为分词的核心类 其对象parser可以对句子进行分词与构建语法树
* 分词模型<br>
  斯坦福分词器提供了许多模型<br> 不同模型针对不同语言和不同情境进行使用<br> 在用分词器进行分词时 需要指定使用的分词模型
* 加载分词模型<br>
  通过静态方法LexicalizedParser.loadModel(model)来加载分词模型<br>在加载成功后会返回一个分词器LexicalizedParser对象parser<br>
* 分词<br>
  通过parser.parse(sentence)对句子进行分词<br>
  分词成功会返回语法树对象
* 语法树<br>
  对一个句子分词后将得到Tree对象root<br> 该对象是这个语法树的根
* 单词节点<br>
  可以通过root.getLeaves()获取到语法树的所有叶子节点<br> 
  即该句子中的所有单词节点
* 依赖关系<br>
  1. 通过parser.treebankLanguagePack()得到语法树的语言包TreebankLanguagePack对象tlp
  2. 通过tlp.grammaticalStructureFactory()得到语法结构工厂GrammaticalStructureFactory对象gsf
  3. 通过gsf.newGrammaticalStructure(root)得到根为root的语法树的语法结构GrammaticalStructure对象gs
  4. 通过gs.typedDependenciesCCprocessed()得到该语法结构中的每个依赖关系对象dependency
  5. 通过dependency.reln()得到语法关系GrammaticalRelation对象gr
  6. 通过dependency.dep() dependency.dep()<br> 得到有依赖关系的两个单词对象<br> 即有语法关系的两个单词对象 
* 遍历语法树<br>
为了实现计算 必须对语法树进行遍历或者在语法树上移动<br>
摸索出两种方法:
    * root.preOrderNodeList()或者root.postOrderNodeList()<br>
    可以直接获得按前序或后序遍历顺序排列的节点列表
    * 首先通过root.children()获取到子节点node集合<br>
    再用node.children()或者node.parent()进行移动<br>
    这种方法更为灵活
* 使用分词器的流程<br>
    ->选定分词模型model<br>
    ->加载分词模型model构建分词器对象parser<br>
    ->用parser对目标句子进行分词得到语法树根root<br>
    ->获取语法树所有叶节点即单词节点<br>
    ->获取语法树对应的所有依赖关系<br>
    ->遍历语法树或在语法树上进行移动根据模型进行计算

### 模型
stanford-parser-3.8.0-models.jar<br>
edu.standford.nlp.models.lexparser包<br>
有这些分词模型<br>
![image](https://note.youdao.com/yws/public/resource/708ad1b1ee41c2b335e0cac5c8d83d03/xmlnote/8214222B34DE4C9E8A7E6F5DDD018B7E/2830)<br>
主要关注每种语言其后缀<br>
大致分为以下类型:
1. Factored
    * 优点<br>
        通用
    * 缺点<br>
        不论是加载还是分词速度都稍慢
    * 该模型计算结果相比论文的结果中最中心的词的权重偏小 其他词的权重偏大 但中心词的权重仍然占最大
2. PCFG
    * 优点<br>
        简小 加载和分词的速度较快 
    * 缺点<br>
        适用范围较窄
    * 该模型计算结果与论文的结果基本一致
3. RNN
    * 该模型加载和分词速度在前两者之间
    * 该模型计算结果与论文结果完全不同
* 建议使用PCFG模型
