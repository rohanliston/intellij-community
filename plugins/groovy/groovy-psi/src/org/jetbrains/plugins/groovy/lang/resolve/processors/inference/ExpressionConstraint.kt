// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.groovy.lang.resolve.processors.inference

import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.resolve.graphInference.constraints.ConstraintFormula
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression

class ExpressionConstraint(private val expression: GrExpression, private val leftType: PsiType?) : GrConstraintFormula() {

  override fun reduce(session: GroovyInferenceSession, constraints: MutableList<ConstraintFormula>): Boolean {
    when (expression) {
      is GrMethodCall -> {
        val invokedExpression = expression.invokedExpression as? GrReferenceExpression ?: return true
        constraints.add(ReferenceExpressionConstraint(invokedExpression, leftType))
      }
      is GrClosableBlock -> if (leftType != null) constraints.add(ClosureConstraint(expression, leftType))
      else -> if (leftType != null) constraints.add(TypeConstraint(leftType, expression.type, expression))
    }
    return true
  }
}
