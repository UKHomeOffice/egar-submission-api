/*******************************************************************************
 * Copyright (c) 2013,2014 Rüdiger Herrmann
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 *   Matt Morrissette - allow to use non-static inner IgnoreConditions
 *
 * @see http://www.codeaffine.com/2013/11/18/a-junit-rule-to-conditionally-ignore-tests/
 * @see https://gist.github.com/rherrmann/7447571
 *
 ******************************************************************************/
package uk.co.civica.microservice.util.testing.utils;

import lombok.Builder;
import lombok.Data;
import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

public class ConditionalIgnoreRule implements MethodRule {

  public interface IgnoreCondition {
    boolean shouldIgnore();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE,ElementType.METHOD})
  public @interface ConditionalIgnore {
    Class<? extends IgnoreCondition> condition();
  }

  @Override
  public Statement apply( Statement baseStatement, FrameworkMethod method, Object target ) {
    Statement resultStatement = baseStatement;
    if( hasConditionalIgnoreAnnotation( method , target ) ) {
    	IgnoreConditionHolder holder = getIgnoreContition( target, method );
      if( holder.getIgnoreCondition().shouldIgnore() ) {
        resultStatement = new IgnoreStatement( holder );
      }
    }
    return resultStatement;
  }

  private static boolean hasConditionalIgnoreAnnotation( FrameworkMethod method, Object target ) {
    return ( method.getAnnotation( ConditionalIgnore.class ) != null )
    		||
    		( method.getDeclaringClass().getAnnotation( ConditionalIgnore.class ) != null )
    		||
    		( target.getClass().getAnnotation( ConditionalIgnore.class ) != null )
    		;
  }

  @Data
  @Builder
  private static class IgnoreConditionHolder
  {
	  private IgnoreCondition ignoreCondition ;
	  private String annotationOn;
  }
  
  
  private static IgnoreConditionHolder getIgnoreContition( Object target, FrameworkMethod method ) {
	  
	String annotationOn =  "method";
	ConditionalIgnore annotation = method.getAnnotation( ConditionalIgnore.class );
    
    if ( annotation == null ) 
    	{
    	annotationOn =  "class (" + method.getDeclaringClass().getSimpleName() + ")";
    	annotation = method.getDeclaringClass().getAnnotation( ConditionalIgnore.class );
    	}
    if ( annotation == null ) 
    	{
    	annotationOn =  "class (" + target.getClass().getSimpleName() + ")";
    	annotation = target.getClass().getAnnotation( ConditionalIgnore.class );
    	}
    
    IgnoreCondition ignoreCondition = new IgnoreConditionCreator( target, annotation ).create();
    return IgnoreConditionHolder
    			.builder()
    				.ignoreCondition(ignoreCondition)
    				.annotationOn(annotationOn)
    			.build();
  }

  private static class IgnoreConditionCreator {
	  
    private final Object target;
    private final Class<? extends IgnoreCondition> conditionType;

    IgnoreConditionCreator( Object target, ConditionalIgnore annotation ) {
      this.target = target;
      this.conditionType = annotation.condition();
    }

    IgnoreCondition create() {
      checkConditionType();
      try {
        return createCondition();
      } catch( RuntimeException re ) {
        throw re;
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }
    }

    private IgnoreCondition createCondition() throws Exception {
      IgnoreCondition result;
      if( isConditionTypeStandalone() ) {
        result = conditionType.newInstance();
      } else {
        result = conditionType.getDeclaredConstructor( target.getClass() ).newInstance( target );
      }
      return result;
    }

    private void checkConditionType() {
      if( !isConditionTypeStandalone() && !isConditionTypeDeclaredInTarget() ) {
        String msg
          = "Conditional class '%s' is a member class "
          + "but was not declared inside the test case using it.\n"
          + "Either make this class a static class, "
          + "standalone class (by declaring it in it's own file) "
          + "or move it inside the test case using it";
        throw new IllegalArgumentException( String.format ( msg, conditionType.getName() ) );
      }
    }

    private boolean isConditionTypeStandalone() {
      return !conditionType.isMemberClass() || Modifier.isStatic( conditionType.getModifiers() );
    }

    private boolean isConditionTypeDeclaredInTarget() {
      return target.getClass().isAssignableFrom( conditionType.getDeclaringClass() );
    }
  }

  private static class IgnoreStatement extends Statement {
    private final IgnoreConditionHolder holder;

    IgnoreStatement( IgnoreConditionHolder holder ) {
      this.holder = holder;
    }

    @Override
    public void evaluate() {
      Assume.assumeTrue( "Conditionally ignored by " + holder.getIgnoreCondition().getClass().getSimpleName() + ", on " + holder.getAnnotationOn() , false );
    }
  }

}