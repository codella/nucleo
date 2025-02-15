package dk.codella.nucleo;

public interface HttpRoutesProvider extends Runnable {
  // TODO: Find a Weld idiomatic way to discover resources that register routes on Vert.x's Route
  // without using this interface
}
