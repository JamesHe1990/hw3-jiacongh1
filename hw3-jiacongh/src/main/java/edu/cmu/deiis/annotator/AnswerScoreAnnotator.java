package edu.cmu.deiis.annotator;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Question;
import org.cleartk.ne.type.NamedEntity;
import org.cleartk.ne.type.NamedEntityMention;


/** 
 * Annotator of Scores of each answer. Use the result of NGramAnnotator.java, QuestionAnnotor.java and AnswerAnnotator.java to calculate the score of each answer. Then save them to AnswerScore data structure. 
 * */
public class AnswerScoreAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Get all answers and their corresponding NGrams. Calculate precision and assign scores with the precision value
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    
    ArrayList<NGram> qstNGramList = getQstNGramList(aJCas);
    ArrayList[] nGramAndAnswer =  getAswNGramList(aJCas);
    ArrayList<ArrayList<NGram>> aswNGramList = nGramAndAnswer[0];
    ArrayList<Answer> ansList = nGramAndAnswer[1];
    //iterate and find match number
    for(int i=0;i<aswNGramList.size();i++){//every answer sentence compare with qst
      ArrayList<NGram>nGramList = aswNGramList.get(i);
      Answer asw=ansList.get(i);
      double matchNum=0;
      for (NGram aswNGram : nGramList) {
        for (NGram qstNGram : qstNGramList) {
          String aswStr = aswNGram.getCoveredText();
          String qst = qstNGram.getCoveredText();
          if(aswStr.equals(qst))
            matchNum++;
        }
      }
      int begin=asw.getBegin();
      int end=asw.getEnd();
      double gramNum = (double)nGramList.size();
      double score = matchNum/gramNum;
      AnswerScore aswScore = new AnswerScore(aJCas,begin,end);
      aswScore.setAnswer(asw);
      aswScore.setScore(score);
      aswScore.addToIndexes();
    }
    //System.out.println("ok");
    //evaluateScore(aJCas);

  }

  /**
   * Find NGrams that belongs to the question and returns a list of NGram objects.
   */
  public ArrayList<NGram> getQstNGramList(JCas aJCas)throws RuntimeException{
    FSIndex qstIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator qstIter = qstIndex.iterator();
    Boolean flag = true; 
    ArrayList<NGram> qstNGramList = new ArrayList<NGram>();//返回它
    while (qstIter.hasNext()) {
      if(!flag){
        throw new RuntimeException("multiple questions in a file");
      }
      flag=!flag;
      Question qst = (Question) qstIter.next();
      int qstBegin=qst.getBegin();
      int qstEnd=qst.getEnd();
      FSIndex nGramIndex = aJCas.getAnnotationIndex(NGram.type);
      Iterator nGramIter = nGramIndex.iterator();
      while (nGramIter.hasNext()) {
        NGram ngram = (NGram)nGramIter.next();
        int begin = ngram.getBegin();
        int end = ngram.getEnd();
        if(begin>=qstBegin && end<=qstEnd){
          qstNGramList.add(ngram);
        }
      }
    }
    return qstNGramList;      
    }
  
  /**
   * Find NGrams that belongs to the answer and returns a list of NGram objects.
   */
  public ArrayList[] getAswNGramList(JCas aJCas){
    FSIndex aswIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator aswIter = aswIndex.iterator();
    ArrayList nGramAndAns[]=new ArrayList[2];
    ArrayList<ArrayList<NGram>> aswNGramList = new ArrayList<ArrayList<NGram>>();//返回它
    ArrayList<Answer>aswList=new ArrayList<Answer>();
    int i=0;
    while(aswIter.hasNext()){
      Answer asw = (Answer) aswIter.next();
      //System.out.println("answer:"+asw.getCoveredText()+"  i:"+(i++));
      ArrayList<NGram> nGramList = new ArrayList<NGram>();
      int aswBegin = asw.getBegin();
      int aswEnd = asw.getEnd();
      FSIndex nGramIndex = aJCas.getAnnotationIndex(NGram.type);
      Iterator nGramIter = nGramIndex.iterator();
      
      while (nGramIter.hasNext()) {
        NGram ngram = (NGram)nGramIter.next();
        int begin = ngram.getBegin();
        int end = ngram.getEnd();
        if(begin>=aswBegin && end<=aswEnd){
          nGramList.add(ngram);
        }
      }
      aswList.add(asw);
      aswNGramList.add(nGramList);
    }
    nGramAndAns[0] = aswNGramList;
    nGramAndAns[1] = aswList;
    
    return nGramAndAns;
  }
  
/*
  public void evaluateScore(JCas aJCas){
    FSIndex aswScoreIndex = aJCas.getAnnotationIndex(AnswerScore.type);
    Iterator aswScoreIter = aswScoreIndex.iterator();
    ArrayList <AnswerScore> al=new ArrayList<AnswerScore>();
    ArrayList <AnswerScore> alnew= new ArrayList<AnswerScore>();
    while(aswScoreIter.hasNext()){
      AnswerScore aswScore = (AnswerScore) aswScoreIter.next();
      al.add(aswScore);
      }
    int length=al.size();
    
    FSIndex qstIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator qstIter = qstIndex.iterator();
    while ( qstIter.hasNext()){
      Question qst = (Question)qstIter.next();
      System.out.println("");
      System.out.println("Question: ["+qst.getCoveredText()+"]");
      System.out.println("Answers and Scores:");
    }
    
    for(int i=0;i<length;i++){
      double score=0;int position=0;
      for(int j=0;j<al.size();j++){
        AnswerScore aswScore = al.get(j);
        double scoreNum=aswScore.getScore();
        if(scoreNum>score){
          position=j;
          score=aswScore.getScore();
        }
      }
      AnswerScore aswSc=al.get(position);
      alnew.add(i, aswSc);
      al.remove(position);
      boolean x=aswSc.getAnswer().getIsCorrect();
      int standard=0;
      if(x)
        standard=1;
      System.out.println(standard+"   Score:"+String.format("%1$.2f",aswSc.getScore())+aswSc.getCoveredText());

    }
  }
*/
}

