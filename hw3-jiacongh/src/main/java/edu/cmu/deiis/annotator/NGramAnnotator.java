package edu.cmu.deiis.annotator;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Token;

public class NGramAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Not used in this project
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    
    
    FSIndex answerIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator<Answer> answerIter = answerIndex.iterator();
    
    FSIndex questionIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator<Question> questionIter = questionIndex.iterator();
    
    FSIndex tokenIndex = aJCas.getAnnotationIndex(Token.type);
    Iterator tokenIter = tokenIndex.iterator();
/*    while(tokenIter.hasNext()){
      Token tkn =(Token) tokenIter.next();
      System.out.println(tkn.getCoveredText());
    }
*/

    //addNgramQuestion(aJCas, questionIter,tokenIter);
    //addNgram(aJCas, answerIter, tokenIter);
  }
  
  public void addNgram(JCas aJCas,Iterator answerIter,Iterator tokenIter){
    while(answerIter.hasNext()){
      Answer asw = (Answer) answerIter.next();
      ArrayList <Token> alNGram = new ArrayList<Token>();
      while(tokenIter.hasNext()){
        Token tkn = (Token) tokenIter.next();
        if (asw.getBegin()<=tkn.getBegin()&&asw.getEnd()>=tkn.getEnd())
          alNGram.add(tkn);
      }
      int num = alNGram.size();
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
  

  public void addNgramQuestion(JCas aJCas,Iterator questionIter,Iterator tokenIter){
    while(questionIter.hasNext()){
      Question qst = (Question) questionIter.next();
      ArrayList <Token> alNGram = new ArrayList<Token>();
      while(tokenIter.hasNext()){
        Token tkn = (Token) tokenIter.next();
        if (qst.getBegin()<=tkn.getBegin()&&qst.getEnd()>=tkn.getEnd())
          alNGram.add(tkn);
      }
      int num = alNGram.size();
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
  

}
