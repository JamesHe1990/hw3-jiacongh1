package edu.cmu.deiis.annotator;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Sentence;

/** 
 * Annotator of Answer.  
 * Use the result of SentenceAnnotator.java, 
 * analyze them if find Answer and save them to Answer object
 */
public class AnswerAnnotator extends JCasAnnotator_ImplBase {
  private Pattern pAnswer;
  
  /**
   * initialize parameters and assign pattern regex its corresponding value.
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    // Get config. parameter values
    String answerPattern = (String) aContext.getConfigParameterValue("answerPattern");
    pAnswer = Pattern.compile(answerPattern);
    
  }

  /**
   * Recogonize the answer if a sentence start with word "A". 
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIndex sentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
    Iterator sentenceIter = sentenceIndex.iterator();
    while(sentenceIter.hasNext()){
      Sentence sentence = (Sentence) sentenceIter.next();
      String sent = sentence.getCoveredText();
      
      if(sent.startsWith("A ")){
        Matcher m = pAnswer.matcher(sent);
        if(m.find()){
          String group=m.group();
          //System.out.println("group:"+group);
          int start = m.end();
          int begin = sentence.getBegin()+start;
          int end = sentence.getEnd(); 
          Answer asw = new Answer(aJCas, begin, end);
          if(group.contains("1")){
            asw.setIsCorrect(true);
          }else{
            asw.setIsCorrect(false);
          }
          asw.addToIndexes();
        }
        //int begin = sentence.getBegin();
        //int end = sentence.getEnd();
      }
    }

  }

  /**
   * @param args
   */
 

}
