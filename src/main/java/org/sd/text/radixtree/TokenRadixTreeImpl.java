/*
    Copyright 2009 Semantic Discovery, Inc. (www.semanticdiscovery.com)

    This file is part of the Semantic Discovery Toolkit.

    The Semantic Discovery Toolkit is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Semantic Discovery Toolkit is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with The Semantic Discovery Toolkit.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.sd.text.radixtree;


import org.sd.util.tree.Tree;

import java.util.*;


/**
 * Implementation ported to use the org.sd.util.tree.Tree datastructure.
 * <p>
 * Using the Tree enables us to apply reverse operations to the radix tree,
 * which allow us to do things like longest substring matches.
 * <p>
 * Ported from RadixTreeImpl by:
 * <p>
 * author Tahseen Ur Rehman
 * email: tahseen.ur.rehman {at.spam.me.not} gmail.com
 *
 * @author Spence Koehler
 * @author Jeff Tsay
 */
public class TokenRadixTreeImpl<T, V> implements TokenRadixTree<T, V> {

  private Tree<TokenRadixData<T, V>> root;

  private long size;

  /**
   * Create a Radix Tree with only the default node root.
   */
  public TokenRadixTreeImpl() {
    root = new Tree<TokenRadixData<T, V>>(new TokenRadixData<T, V>(
      Collections.<T>emptyList(), false, null));
    size = 0;
  }

  /**
   * Get this tree's root.
   */
  public Tree<TokenRadixData<T, V>> getRoot() {
    return root;
  }

  /**
   * Insert a new string key and its value to the tree. Throw an IllegalStateException
   * if there is a conflict.
   *
   * @param tokens
   *            The tokens of the object
   * @param value
   *            The value that need to be stored corresponding to the given
   *            key.
   */
  @Override
  public void insert(List<T> tokens, V value) {
    insert(tokens, value, null, null);
  }

  /**
   * Insert a new string key and its value to the tree. If there is already
   * a conflicting value at the insertion point, resolve by calling the
   * value merger function.
   *
   * @param tokens
   *            The tokens of the object
   * @param value
   *            The value that need to be stored corresponding to the given
   *            key.
   * @param valueMerger
   *            The value merger to use to resolve conflicts. If null, the
   *            conflict will generate an IllegalStateException.
   * @param valueReplicator
   *            The value replicator to use to make a duplicate of a value
   *            to place into nodes inserted for splits. If null, the
   *            same instance of value will be used. For values like Strings,
   *            using the same value instance does no harm as the string's
   *            contents are immutable. A value whose object carries state
   *            that does change (i.e. when merged,) however, must be
   *            duplicated for proper function.
   */
  @Override
  public void insert(List<T> tokens, V value, ValueWithTokensMerger<T, V> valueMerger, ValueReplicator<V> valueReplicator) {
    try {
      insert(tokens, root, value, valueMerger, valueReplicator);
    }
    catch (IllegalStateException e) {
      // re-throw the exception with 'key' in the message
      throw new IllegalStateException("Duplicate key: '" + tokens + "'");
    }
    size++;
  }

  /**
   * Delete tokens and their associated value from the tree.
   * @param tokens The tokens of the node that need to be deleted
   * @return true if deleted
   */
  @Override
  public boolean delete(List<T> tokens) {
    final boolean[] delete = new boolean[]{false};

    final TokenTreeVisitor<T, V> visitor = new TokenTreeVisitor<T, V>() {
      @Override
      public void visit(List<T> tokens, Tree<TokenRadixData<T, V>> node) {
        final Tree<TokenRadixData<T, V>> parent = node.getParent();
        if (parent == null) return;

        final TokenRadixData<T, V> data = node.getData();

        delete[0] = data.isReal();

        // if it is a real node
        if (delete[0]) {

          // If there no children of the node we need to
          // delete it from the its parent children list
          if (node.numChildren() == 0) {
            node.prune(true, true);

            // if parent is not real node and has only one child
            // then they need to be merged.
            if (parent.numChildren() == 1 && !parent.getData().isReal()) {
              mergeNodes(parent, parent.getChildren().get(0));
            }
          }
          else if (node.numChildren() == 1) {
            // we need to merge the only child of this node with
            // itself
            mergeNodes(node, node.getChildren().get(0));
          }
          else { // we just need to mark the node as non-real.
            data.setReal(false);
          }
        }
      }

      /**
       * Merge a child into its parent node. Operation only valid if it is
       * only child of the parent node and parent node is not a real node.
       *
       * @param parent
       *            The parent Node
       * @param child
       *            The child Node
       */
      private void mergeNodes(Tree<TokenRadixData<T, V>> parent, Tree<TokenRadixData<T, V>> child) {
        final TokenRadixData<T, V> parentData = parent.getData();
        final TokenRadixData<T, V> childData = child.getData();

        final List<T> updatedTokens = new ArrayList<T>(parentData.getTokens());
        updatedTokens.addAll(childData.getTokens());

        parentData.setTokens(updatedTokens);
        parentData.setReal(childData.isReal());
        parentData.setValue(childData.getValue());

        // delete the child, moving its children to the parent
        final List<Tree<TokenRadixData<T, V>>> children = child.getChildren();
        child.prune(true, true);
        if (children != null) {
          for (Tree<TokenRadixData<T, V>> gchild : children) {
            parent.addChild(gchild);
          }
        }
      }
    };

    visit(tokens, visitor);

    if(delete[0]) {
      size--;
    }

    return delete[0];
  }

  /**
   * Find a value based on its corresponding tokens.
   *
   * @param tokens The tokens for which to search the tree.
   * @return The value corresponding to the key. null if the key cannot be found.
   */
  @Override
  public V find(List<T> tokens) {
    final List<V> result = new ArrayList<V>();

    final TokenTreeVisitor<T, V> visitor = new TokenTreeVisitor<T, V>() {

      public void visit(List<T> tokens, Tree<TokenRadixData<T, V>> node) {
        final TokenRadixData<T, V> data = node.getData();

        if (data.isReal()) {
          result.add(data.getValue());
        }
      }
    };

    visit(tokens, visitor);

    return result.size() == 1 ? result.get(0) : null;
  }

  /**
   * Check if the tree contains any entry corresponding to the given key.
   *
   * @param tokens The key that need to be searched in the tree.
   * @return return true if the key is present in the tree otherwise false
   */
  public boolean contains(List<T> tokens) {
    final boolean[] result = new boolean[]{false};

    final TokenTreeVisitor<T, V> visitor = new TokenTreeVisitor<T, V>() {

      public void visit(List<T> tokens, Tree<TokenRadixData<T, V>> node) {
        result[0] = node.getData().isReal();
      }
    };

    visit(tokens, visitor);

    return result[0];
  }

  /**
   * Search for all the keys that start with given prefix. limiting the results based on the supplied limit.
   *
   * @param tokenPrefix The prefix for which keys need to be search
   * @param recordLimit The limit for the results
   * @return The list of values whose key start with the given prefix
   */
  public List<V> searchPrefix(List<T> tokenPrefix, int recordLimit) {
    List<V> values = new ArrayList<V>();

    Tree<TokenRadixData<T, V>> node = searchPrefix(tokenPrefix, root);

    if (node != null) {
      final TokenRadixData<T, V> nodeData = node.getData();
      if (nodeData.isReal()) {
        values.add(nodeData.getValue());
      }
      getNodes(node, values, recordLimit);
    }

    return values;
  }

  /**
   * Return the size of the Radix tree
   * @return the size of the tree
   */
  public long getSize() {
    return size;
  }

  /**
   * Recursively insert the tokens in the radix tree.
   *
   * @param tokens The tokens to be inserted
   * @param node The current node
   * @param value The value associated with the key
   * @param valueMerger The function to use for conflicts. If null, conflicts
   *        cause an IllegalStateException to be thrown.
   * @pram valueReplicator
   *            The value replicator to use to make a duplicate of a value
   *            to place into nodes inserted for splits. If null, the
   *            same instance of value will be used. For values like Strings,
   *            using the same value instance does no harm as the string's
   *            contents are immutable. A value whose object carries state
   *            that does change (i.e. when merged,) however, must be
   *            duplicated for proper function.
   */
  private void insert(List<T> tokens, Tree<TokenRadixData<T, V>> node, V value, ValueWithTokensMerger<T, V> valueMerger, ValueReplicator<V> valueReplicator) {
    final int keylen = tokens.size();
    final TokenRadixData<T, V> nodeData = node.getData();
    final List<T> nodeTokens = nodeData.getTokens();
    final int nodelen = nodeTokens.size();

    final Iterator<T> it = tokens.iterator();
    final Iterator<T> nodeIt = nodeTokens.iterator();

    int i = 0;
    while (i < keylen && i < nodelen) {
      if (!it.next().equals(nodeIt.next())) {
        break;
      }
      i++;
    }

    // we are either at the root node
    // or we need to go down the tree
    if ((i == 0) || (i < keylen && i >= nodelen)) {
      boolean flag = false;
      final List<T> newTokens = tokens.subList(i, keylen);

      if (node.hasChildren()) {
        final T headToken = newTokens.get(0);
        for (Tree<TokenRadixData<T, V>> child : node.getChildren()) {
          final List<T> childTokens = child.getData().getTokens();
          if (!childTokens.isEmpty() &&
              childTokens.get(0).equals(headToken)) {
            flag = true;
            insert(newTokens, child, value, valueMerger, valueReplicator);
            break;
          }
        }
      }

      // just add the node as the child of the current node
      if (!flag) {
        node.addChild(new TokenRadixData<T, V>(newTokens, true, value));
      }
    }

    // there is a exact match just make the current node as data node
    else if (i == keylen && i == nodelen) {
      if (nodeData.isReal()) {
        if (valueMerger == null) {
          throw new IllegalStateException("Duplicate key");
        }
        else {
          valueMerger.merge(nodeData, value);
        }
      }
      else {
        nodeData.setReal(true);
        nodeData.setValue(value);
      }
    }
    // This node need to be split as the key to be inserted
    // is a prefix of the current node key
    else if (i > 0 && i < nodelen) {
      Tree<TokenRadixData<T, V>> n1 =
        new Tree<TokenRadixData<T, V>>(
          new TokenRadixData<T, V>(
            nodeTokens.subList(i, nodelen),
            nodeData.isReal(),
            nodeData.getValue()));

      node.moveChildrenTo(n1);
      nodeData.setTokens(tokens.subList(0, i));
      nodeData.setReal(false);
      node.addChild(n1);  // note: node's value is now also in its child.

      if (i < keylen) {
        node.addChild(new TokenRadixData<T, V>(
         tokens.subList(i, keylen), true, value));

        if (valueReplicator != null) {
          nodeData.setValue(valueReplicator.replicate(nodeData.getValue()));
        }
        //else node's value is referenced in both self and child.
      }
      else {
        nodeData.setValue(value);
        nodeData.setReal(true);
      }
    }
    // this key need to be added as the child of the current node
    else {
      node.addChild(new TokenRadixData<T, V>(
       nodeTokens.subList(i, nodelen), nodeData.isReal(),
       nodeData.getValue()));

      nodeData.setTokens(tokens);
      nodeData.setReal(true);
      nodeData.setValue(value);
    }
  }

  private Tree<TokenRadixData<T, V>> searchPrefix(List<T> tokens, Tree<TokenRadixData<T, V>> node) {
    Tree<TokenRadixData<T, V>> result = null;
    int i = 0;
    final int keylen = tokens.size();
    final TokenRadixData<T, V> nodeData = node.getData();
    final List<T> nodeTokens = nodeData.getTokens();
    final int nodelen = nodeTokens.size();

    final Iterator<T> it = tokens.iterator();
    final Iterator<T> nodeIt = nodeTokens.iterator();

    while (i < keylen && i < nodelen) {
      if (!it.next().equals(nodeIt.next())) {
        break;
      }
      i++;
    }

    if (i == keylen && i <= nodelen) {
      result = node;
    }
    else if ((i < keylen && i >= nodelen) || nodeData.getTokens().isEmpty()) {
      if (node.hasChildren()) {
        List<T> newTokens = tokens.subList(i, keylen);
        final T headToken = newTokens.get(0);
        for (Tree<TokenRadixData<T, V>> child : node.getChildren()) {
          final List<T> childTokens = child.getData().getTokens();
          if (!childTokens.isEmpty() &&
              childTokens.get(0).equals(headToken)) {
            result = searchPrefix(newTokens, child);
            break;
          }
        }
      }
    }

    return result;
  }

  private void getNodes(Tree<TokenRadixData<T, V>> parent, List<V> keys, int limit) {
    if (!parent.hasChildren()) return;

    final Queue<Tree<TokenRadixData<T, V>>> queue = new LinkedList<Tree<TokenRadixData<T, V>>>();

    queue.addAll(parent.getChildren());

    while (!queue.isEmpty()) {
      final Tree<TokenRadixData<T, V>> node = queue.remove();
      final TokenRadixData<T, V> nodeData = node.getData();
      if (nodeData.isReal()) {
        keys.add(nodeData.getValue());
      }

      if (keys.size() == limit) {
        break;
      }

      if (node.hasChildren()) {
        queue.addAll(node.getChildren());
      }
    }
  }

  /**
   * visit the node whose tokens matches the given tokens
   * @param tokens The tokens that need to be visited
   * @param visitor The visitor object
   */
  public void visit(List<T> tokens, TokenTreeVisitor<T, V> visitor) {
    if (root != null) {
      visit(tokens, visitor, root);
    }
  }

  /**
   * recursively visit the tree based on the supplied "key". calls the TokenTreeVisitor
   * for the node whose key matches the given prefix
   *
   * @param tokensPrefix
   *            The token prefix (or entire token list) to search in the tree
   * @param visitor
   *            The TokenTreeVisitor that will be called if a node with "key" as its
   *            key is found
   * @param node
   *            The Node from where onward to search
   */
  private void visit(List<T> tokensPrefix, TokenTreeVisitor<T, V> visitor, Tree<TokenRadixData<T, V>> node) {
    final int keylen = tokensPrefix.size();
    final TokenRadixData<T, V> nodeData = node.getData();
    final List<T> nodeTokens = nodeData.getTokens();
    final int nodelen = nodeTokens.size();

    // match the prefix with node key
    final Iterator<T> it = tokensPrefix.iterator();
    final Iterator<T> nodeIt = nodeTokens.iterator();

    int i = 0;
    while (i < keylen && i < nodelen) {
      if (!it.next().equals(nodeIt.next())) {
        break;
      }
      i++;
    }

    // if the node key and prefix match, we found a match!
    if (i == keylen && i == nodelen) {
      visitor.visit(tokensPrefix, node);
    }
    else if ((i < keylen && i >= nodelen) || // either we need to traverse the children
             nodeTokens.isEmpty()) { // OR we are at the root
      if (node.hasChildren()) {
        final List<T> newTokens = tokensPrefix.subList(i, keylen);
        final T headToken = newTokens.get(0);

        for (Tree<TokenRadixData<T, V>> child : node.getChildren()) {
          // recursively search the child nodes
          final List<T> childTokens = child.getData().getTokens();
          if (!childTokens.isEmpty() &&
              childTokens.get(0).equals(headToken)) {
            visit(newTokens, visitor, child);
            break;
          }
        }
      }
    }
  }
}
