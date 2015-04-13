/**
 * @file   WalkDepthFirst.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Apr  5 21:13:34 2015
 *
 * @brief  A helper class for depth first traversal.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Constructs positions by depth first walk.
 */

public class WalkDepthFirst {

  private class OrderComparator<T> implements Comparator<T> {
    private final List<T> order;

    public OrderComparator(final List<T> order) {
      this.order = order;
    }

    @Override
    public int compare(final T object1, final T object2) {
      final Integer index1 = this.order.indexOf(object1);
      final Integer index2 = this.order.indexOf(object2);
      return -1 * Integer.compare(index1, index2);
    }
  }

  private final ComponentsPositions positions = new ComponentsPositions();
  private List<RichStructure<?>> order = null;
  private RichStructure<?> start = null;

  public WalkDepthFirst(final List<RichStructure<?>> order) {
    this.order = order;
    this.start();
    this.walkDepthFirst();
  }

  public WalkDepthFirst(final RichStructure<?> start,
      final List<RichStructure<?>> order) {
    this.start = start;
    this.order = order;
    this.walkDepthFirst();
  }

  private final void start() {
    if (this.order.isEmpty()) {
      return;
    }
    this.start = this.order.get(0);
  }

  /**
   * Depth first traversal of structure.
   */
  protected final void walkDepthFirst() {
    final OrderComparator<RichStructure<?>> comparator = new OrderComparator<>(
        this.order);
    final List<RichStructure<?>> visited = new ArrayList<RichStructure<?>>();
    final Stack<RichStructure<?>> frontier = new Stack<RichStructure<?>>();
    frontier.push(this.start);
    while (!frontier.empty()) {
      final RichStructure<?> current = frontier.pop();
      if (visited.contains(current)) {
        continue;
      }
      visited.add(current);
      this.positions.addNext(current.getId());
      final List<RichStructure<?>> elements = current.getConnections().stream()
          .map(con -> RichStructureHelper.getRichStructure(con.getConnected()))
          .filter(this.order::contains).collect(Collectors.toList());
      Collections.sort(elements, comparator);
      elements.stream().forEach(frontier::push);
    }
  }

  public final ComponentsPositions getPositions() {
    return this.positions;
  }
}
