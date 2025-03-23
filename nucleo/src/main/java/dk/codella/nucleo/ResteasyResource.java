package dk.codella.nucleo;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

import java.io.Serial;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
public @interface ResteasyResource {
  public static final class Literal extends AnnotationLiteral<ResteasyResource> implements ResteasyResource {
    public static final Literal INSTANCE = new Literal();
    @Serial
    private static final long serialVersionUID = 72716872346L;

    public Literal() {
    }
  }
}
