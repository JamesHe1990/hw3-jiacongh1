package edu.cmu.deiis.annotator;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceProcessException;

import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.Question;

public class Consumer extends CasConsumer_ImplBase {

  @Override


  
  public void processCas(CAS aCAS) throws ResourceProcessException {
    JCas aJCas;
    try {
      aJCas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
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
      System.out.println(standard+"  "+aswSc.getCoveredText()+"   Score:"+String.format("%1$.2f",aswSc.getScore()));
    }
    

  }

  

}
