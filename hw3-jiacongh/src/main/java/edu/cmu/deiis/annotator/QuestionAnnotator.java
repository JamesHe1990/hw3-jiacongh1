package edu.cmu.deiis.annotator;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tutorial.Meeting;
import org.apache.uima.tutorial.RoomNumber;

import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Sentence;

/** 
 * Annotator of Question. Use the result of SentenceAnnotator.java, 
 * analyze them if find Question and save them to Answer object
 * */

public class QuestionAnnotator extends JCasAnnotator_ImplBase {
  
  /**
   * Get each Sentence object and analyze it   If contain "Q" then put the text into Question object
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIndex sentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator sentenceIter = sentenceIndex.iterator();
    while(sentenceIter.hasNext()){
      //RoomNumber room = (RoomNumber) roomNumberIter.next();
      Sentence sentence = (Sentence) sentenceIter.next();
      String sent = sentence.getCoveredText();
      int begin = sentence.getBegin();
      int end = sentence.getEnd();
      
      if(sent.startsWith("Q")){
        //System.out.println("found");
        Question qst = new Question(aJCas, begin+2, end);
        qst.addToIndexes();
      }
    }

  }

  /**
   * @param args
   */

}
