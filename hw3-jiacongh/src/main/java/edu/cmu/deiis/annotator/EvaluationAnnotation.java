package edu.cmu.deiis.annotator;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.cleartk.ne.type.NamedEntityMention;
import org.cleartk.token.type.Token;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.Question;


/** 
 * Last phase in the pipe line and sort each answer by their score and type them out
*/

public class EvaluationAnnotation extends JCasAnnotator_ImplBase {

  /**
   * Get all AnswerScore objects, sort them by score and print the sorted documents to the console. 
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex aswScoreIndex = aJCas.getAnnotationIndex(AnswerScore.type);
    Iterator aswScoreIter = aswScoreIndex.iterator();
    
    /***********************************************************************/
    FSIndex tokenIndex = aJCas.getAnnotationIndex(Token.type);
    Iterator tokenIter = tokenIndex.iterator();
    while(tokenIter.hasNext()){
      Token tkn =(Token) tokenIter.next();
      System.out.println(tkn.getCoveredText());
    }
    /***********************************************************************/
    
    
    
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
      //System.out.println("what the fuck");
    }
/*    FSIndex TokenIndex = aJCas.getAnnotationIndex(Token.type);
    Iterator tokenIter = TokenIndex.iterator();
    while (tokenIter.hasNext()) {
      System.out.println("sadf");
    }
    */
    //printNamedEntity(aJCas);

  }

  /**
   * @param args
   */

  
  public void printNamedEntity(JCas aJcas){
    //org.apache.uima.jcas.tcas.Annotation document = (org.apache.uima.jcas.tcas.Annotation) aJcas.getDocumentAnnotationFs();
    
    System.out.println("Print Name Entity Starts------------------------------!");
    FSIndex nameEntityIndex = aJcas.getAnnotationIndex(NamedEntityMention.type);
    FSIndex TokenIndex = aJcas.getAnnotationIndex(Token.type);

    // Iterator to get each sentence annotation
    Iterator nameEntityIter = nameEntityIndex.iterator();
    Iterator tokenIter = TokenIndex.iterator();
    
    while (tokenIter.hasNext()) {
      NamedEntityMention namedEntity = (NamedEntityMention) tokenIter.next();
//      
      String line = namedEntity.getCoveredText();
//      //int x= namedEntity.getBegin();
      System.out.println(""+line);
      org.cleartk.token.type.Token token =(org.cleartk.token.type.Token) tokenIter.next();
      System.out.println(token.getPos());
      if(token.getPos().equals("NNP"));
            System.out.println("namedEntity: " + token.toString());
    }
    System.out.println("Print Name Entity Ends------------------------------!");
//    while(stanfordEntityIter.hasNext())
//    {
//      org.cleartk.token.type.Token token =(org.cleartk.token.type.Token) stanfordEntityIter.next();
//      if(token.getPos().equals("NNP"));
//        System.out.println("namedEntity: " + token.toString());
//    }

  
  }
  
}
