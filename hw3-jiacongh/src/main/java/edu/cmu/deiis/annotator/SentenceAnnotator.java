package edu.cmu.deiis.annotator;

import java.text.BreakIterator;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.examples.tokenizer.Token;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.Sentence;

/** 
 * Annotator of Sentence. Analyse each line of the input data and save them to 
 * Sentence data structure. 
 * */
public class SentenceAnnotator extends JCasAnnotator_ImplBase {
  
  static abstract class Maker {
    abstract Sentence newAnnotation(JCas jcas, int start, int end);
  }

  JCas jcas;

  String input;

  ParsePosition pp = new ParsePosition(0);

  // ****************************************
  // * Static vars holding break iterators
  // ****************************************
  static final BreakIterator sentenceBreak = BreakIterator.getSentenceInstance(Locale.US);

  static final BreakIterator wordBreak = BreakIterator.getWordInstance(Locale.US);

  // *********************************************
  // * function pointers for new instances *
  // *********************************************
  static final Maker sentenceAnnotationMaker = new Maker() {
    Sentence newAnnotation(JCas jcas, int start, int end) {
      return new Sentence(jcas, start, end);
    }
  };
/*
  static final Maker tokenAnnotationMaker = new Maker() {
    Annotation newAnnotation(JCas jcas, int start, int end) {
      return new Token(jcas, start, end);
    }
  };
*/
  // *************************************************************
  // * process *
  // *************************************************************
  /**
   * Analyze the document by each line and cut out its line break
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    jcas = aJCas;
    input = jcas.getDocumentText();

    // Create Annotations
    makeAnnotations(sentenceAnnotationMaker, sentenceBreak);
    //makeAnnotations(tokenAnnotationMaker, wordBreak);
  }

  // *************************************************************
  // * Helper Methods *
  // *************************************************************
  void makeAnnotations(Maker m, BreakIterator b) {
    b.setText(input);
    for (int end = b.next(), start = b.first(); end != BreakIterator.DONE; start = end, end = b
            .next()) {
      // eliminate all-whitespace tokens
      boolean isWhitespace = true;
      for (int i = start; i < end; i++) {
        if (!Character.isWhitespace(input.charAt(i))) {
          isWhitespace = false;
          break;
        }
      }
      if (!isWhitespace) {
        m.newAnnotation(jcas, start, end-2).addToIndexes();//////»»ÐÐ·û
      }
    }
  }
  
}
