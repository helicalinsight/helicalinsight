/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.utility;

/**
 * Created by author on 06-03-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class Pair<T> {

    private T left;

    private T right;

    public Pair(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = right;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public java.lang.String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Pair<?> pair = (Pair<?>) other;
        //noinspection SimplifiableIfStatement
        if (left != null ? !left.equals(pair.left) : pair.left != null) {
            return false;
        }
        return !(right != null ? !right.equals(pair.right) : pair.right != null);
    }

    public boolean contains(T node) {
        return (node != null) && (left.equals(node) || right.equals(node));
    }

    public boolean isIdentical(Pair<T> pair) {
        return (this == pair) || !(pair == null || getClass() != pair.getClass()) && ((pair.left.equals(this.left))
                && (pair.right.equals(this.right)) || (pair.left.equals(this.right)) && (pair.right.equals(this.left)));
    }

    public void swap() {
        T temp = this.left;
        this.left = right;
        this.right = temp;
    }

    public T find(T aNode) {
        if (aNode == null) {
            throw new IllegalArgumentException("Null argument is not allowed");
        }
        if (this.left.equals(aNode)) {
            return this.right;
        } else if (this.right.equals(aNode)) {
            return this.left;
        } else {
            return null;
        }
    }
}
