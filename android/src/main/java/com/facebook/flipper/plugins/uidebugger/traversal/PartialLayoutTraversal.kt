/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.flipper.plugins.uidebugger.traversal

import android.util.Log
import com.facebook.flipper.plugins.uidebugger.LogTag
import com.facebook.flipper.plugins.uidebugger.descriptors.DescriptorRegister
import com.facebook.flipper.plugins.uidebugger.descriptors.Id
import com.facebook.flipper.plugins.uidebugger.descriptors.NodeDescriptor
import com.facebook.flipper.plugins.uidebugger.descriptors.nodeId
import com.facebook.flipper.plugins.uidebugger.model.Node
import com.facebook.flipper.plugins.uidebugger.observers.TreeObserverFactory

/**
 * This will traverse the layout hierarchy until it sees a node that has an observer registered for
 * it.
 * - The first item in the pair is the visited nodes.
 * - The second item are any observable roots discovered.
 */
class PartialLayoutTraversal(
    private val descriptorRegister: DescriptorRegister,
    private val treeObserverFactory: TreeObserverFactory,
) {

  @Suppress("unchecked_cast")
  internal fun NodeDescriptor<*>.asAny(): NodeDescriptor<Any> = this as NodeDescriptor<Any>

  fun traverse(root: Any): Pair<List<Node>, List<Any>> {

    val visited = mutableListOf<Node>()
    val observableRoots = mutableListOf<Any>()

    val stack = mutableListOf<Any>()
    stack.add(root)

    val shallow = mutableSetOf<Any>()

    while (stack.isNotEmpty()) {
      val node = stack.removeLast()

      try {
        // If we encounter a node that has it own observer, don't traverse
        if (node != root && treeObserverFactory.hasObserverFor(node)) {
          observableRoots.add(node)
          continue
        }

        val descriptor = descriptorRegister.descriptorForClassUnsafe(node::class.java).asAny()

        if (shallow.contains(node)) {
          visited.add(
              Node(
                  node.nodeId(),
                  descriptor.getQualifiedName(node),
                  descriptor.getName(node),
                  emptyMap(),
                  descriptor.getBounds(node),
                  emptySet(),
                  emptyList(),
                  null))

          shallow.remove(node)
          continue
        }

        val children = descriptor.getChildren(node)

        val activeChild = descriptor.getActiveChild(node)
        var activeChildId: Id? = null
        if (activeChild != null) {
          activeChildId = activeChild.nodeId()
        }

        val childrenIds = mutableListOf<Id>()
        children.forEach { child ->
          childrenIds.add(child.nodeId())
          stack.add(child)
          // If there is an active child then don't traverse it
          if (activeChild != null && activeChild != child) {
            shallow.add(child)
          }
        }

        val attributes = descriptor.getData(node)
        val bounds = descriptor.getBounds(node)
        val tags = descriptor.getTags(node)
        visited.add(
            Node(
                node.nodeId(),
                descriptor.getQualifiedName(node),
                descriptor.getName(node),
                attributes,
                bounds,
                tags,
                childrenIds,
                activeChildId))
      } catch (exception: Exception) {
        Log.e(LogTag, "Error while processing node ${node.javaClass.name} $node", exception)
      }
    }

    return Pair(visited, observableRoots)
  }
}
