package edu.cmu.deiis.annotator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.Annotation;
import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Sentence;
import edu.cmu.deiis.types.Token;

/** 
 * Annotator of Token. Use the result of both QuestionAnnotator.java 
 * and AnswerAnnotator.java. Analyze question and answers by blanks
 *  and save them to Token object
 * */
public class TokenAnnotator extends JCasAnnotator_ImplBase {

  private Pattern puncPattern;// punctuation

  private Pattern blkPattern;// blank

  private String punctuationPattern;

  
  /**
   * initialize some parameters and assign some pattern regix to the puncPattern regix.
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    // Get config. parameter values
    punctuationPattern = (String) aContext.getConfigParameterValue("puncPattern");

    puncPattern = Pattern.compile(punctuationPattern);
    // System.out.println("punctuationPattern="+punctuationPattern);
  }

  /**
   * Cut out punctuation in sentence, split sentence with blanks and use Token object to save each token.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    // System.out.println("yes");
    FSIndex answerIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator answerIter = answerIndex.iterator();
    // System.out.println("yes");
    while (answerIter.hasNext()) {
      Answer asw = (Answer) answerIter.next();
      String answer = asw.getCoveredText();
      answer = answer.replaceAll(punctuationPattern, "");
      int begin = asw.getBegin();
      String ansToken[] = answer.split(" ");

      ArrayList alAnsNGram = new ArrayList<Token>();//

      for (int i=0;i<ansToken.length;i++) {
        String token=ansToken[i];
        int length = token.length();
        int end = begin + length;
        Token tk = new Token(aJCas, begin, end);
        alAnsNGram.add(tk);
        tk.addToIndexes();
        begin=end+1;

      }
      AnnotateNGram(aJCas, alAnsNGram, answer);
    }
    FSIndex questionIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator questionIter = questionIndex.iterator();
    while (questionIter.hasNext()) {
      Question qst = (Question) questionIter.next();
      String question = qst.getCoveredText();
      question = question.replaceAll(punctuationPattern, "");
      int begin = qst.getBegin();
      // int end = asw.getEnd();
      String qstToken[] = question.split(" ");

      ArrayList alQstNGram = new ArrayList<Token>();//

      for (int i=0;i<qstToken.length;i++) {
        String token = qstToken[i];
        int length = token.length();
        int end = begin + length;
/*        if(i==qstToken.length-1){
          end=end-1;   ///////////»»ÐÐ·û
        }*/
        Token tk = new Token(aJCas, begin, end);        
        alQstNGram.add(tk);
        tk.addToIndexes();
        //System.out.println("begin:" + begin + "end:" + end);
        //begin += (length + 1);
        begin=end+1;
      }
      AnnotateNGram(aJCas, alQstNGram, question);
    }
    
    
  }

  /**
   * Connect several tokens in one sentence to an NGram structure.
   */
  protected void AnnotateNGram(JCas aJCas, ArrayList<Token> alNGram, String text) {
    int num = alNGram.size();
    //System.out.println("alNGram:"+alNGram+"  num:"+num);
    for (int startPos = 0; startPos < num ; startPos++) {
      for (int gramNum = 1; gramNum <= num-startPos && gramNum<=3; gramNum++) {
        Token token1 = alNGram.get(startPos);
        Token token2 = alNGram.get(startPos+gramNum-1);//3=1+3-1
        int start = token1.getBegin();
        int end = token2.getEnd();
        NGram ngramItem = new NGram(aJCas, start, end);
        //List<Token> al = alNGram.subList(startPos, startPos+gramNum-1);
        //ngramItem.setElements(al);
        FSArray v = new FSArray(aJCas,gramNum);
        
        for (int i = startPos,j=0; i < startPos+gramNum; i++,j++) {
          Token tk=alNGram.get(i);
          //ngramItem.setElements((i-startPos), tk);
          v.set(j, tk);
        }
        ngramItem.setElements(v);
        ngramItem.addToIndexes();

      }
    }
  }

}
