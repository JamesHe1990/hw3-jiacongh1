package edu.cmu.deiis.annotator;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Token;

public class NGramAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Not used in this project
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIndex answerIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator answerIter = answerIndex.iterator();
    while(answerIter.hasNext()){
      Answer asw = (Answer) answerIter.next();
      
      
    }

  }

  /**
   * @param args
   */

}
