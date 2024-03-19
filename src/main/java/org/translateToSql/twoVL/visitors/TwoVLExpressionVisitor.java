package org.translateToSql.twoVL.visitors;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import org.translateToSql.*;
import org.translateToSql.utils.ChildPosition;
import org.translateToSql.utils.ParentNode;

public class TwoVLExpressionVisitor implements ToSqlExpressionVisitor {

    private VisitorManager visitorManager;
    private ParentNode parentNode = new ParentNode();

    @Override
    public void visit(BitwiseRightShift aThis) {
        visitBinaryExpression(aThis);
    }

    @Override
    public void visit(BitwiseLeftShift aThis) {
        visitBinaryExpression(aThis);
    }

    @Override
    public void visit(NullValue nullValue) {}

    @Override
    public void visit(Function function) {
        if (function.getParameters() != null) {
            function.getParameters().accept(this);
        }
        if (function.getKeep() != null) {
            function.getKeep().accept(this);
        }
        if (function.getOrderByElements() != null) {
            for (OrderByElement orderByElement : function.getOrderByElements()) {
                orderByElement.getExpression().accept(this);
            }
        }
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        signedExpression.getExpression().accept(this);
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {}

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {}

    @Override
    public void visit(DoubleValue doubleValue) {}

    @Override
    public void visit(LongValue longValue) {}

    @Override
    public void visit(HexValue hexValue) {}

    @Override
    public void visit(DateValue dateValue) {}

    @Override
    public void visit(TimeValue timeValue) {}

    @Override
    public void visit(TimestampValue timestampValue) {}

    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }

    @Override
    public void visit(StringValue stringValue) {}

    @Override
    public void visit(Addition addition) {
        visitBinaryExpression(addition);
    }

    @Override
    public void visit(Division division) {
        visitBinaryExpression(division);
    }

    @Override
    public void visit(IntegerDivision division) {
        visitBinaryExpression(division);
    }

    @Override
    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication);
    }

    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction);
    }

    @Override
    public void visit(AndExpression andExpression) {}

    @Override
    public void visit(OrExpression orExpression) {}

    @Override
    public void visit(XorExpression xorExpression) {
        visitBinaryExpression(xorExpression);
    }

    @Override
    public void visit(Between between) {
        this.setParentNode(between.getLeftExpression(), ChildPosition.LEFT);
        between.getLeftExpression().accept(this);
    }

    @Override
    public void visit(OverlapsCondition overlapsCondition) {}

    @Override
    public void visit(EqualsTo equalsTo) {}

    @Override
    public void visit(GreaterThan greaterThan) {}

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {}

    @Override
    public void visit(InExpression inExpression) {}

    @Override
    public void visit(FullTextSearch fullTextSearch) {}

    @Override
    public void visit(IsNullExpression isNullExpression) {}

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {}

    @Override
    public void visit(LikeExpression likeExpression) {
        this.setParentNode(likeExpression, ChildPosition.LEFT);
        likeExpression.getLeftExpression().accept(this);
    }

    @Override
    public void visit(MinorThan minorThan) {}

    @Override
    public void visit(MinorThanEquals minorThanEquals) {}

    @Override
    public void visit(NotEqualsTo notEqualsTo) {}

    @Override
    public void visit(DoubleAnd doubleAnd) {
        visitBinaryExpression(doubleAnd);
    }

    @Override
    public void visit(Contains contains) {}

    @Override
    public void visit(ContainedBy containedBy) {}

    @Override
    public void visit(ParenthesedSelect selectBody) {
        selectBody.accept(this.getVisitorManager().getSelectVisitor());
    }

    @Override
    public void visit(Column tableColumn) {}

    @Override
    public void visit(CaseExpression caseExpression) {}

    @Override
    public void visit(WhenClause whenClause) {}

    @Override
    public void visit(ExistsExpression existsExpression) {}

    @Override
    public void visit(MemberOfExpression memberOfExpression) {}

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {}

    @Override
    public void visit(Concat concat) {}

    @Override
    public void visit(Matches matches) {}

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd);
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr);
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor);
    }

    @Override
    public void visit(CastExpression cast) {
        setParentNode(cast, ChildPosition.LEFT);
        cast.getLeftExpression().accept(this);
    }

    @Override
    public void visit(Modulo modulo) {
        visitBinaryExpression(modulo);
    }

    @Override
    public void visit(AnalyticExpression aexpr) {}

    @Override
    public void visit(ExtractExpression eexpr) {}

    @Override
    public void visit(IntervalExpression iexpr) {}

    @Override
    public void visit(OracleHierarchicalExpression oexpr) {}

    @Override
    public void visit(RegExpMatchOperator rexpr) {}

    @Override
    public void visit(JsonExpression jsonExpr) {}

    @Override
    public void visit(JsonOperator jsonExpr) {}

    @Override
    public void visit(UserVariable var) {}

    @Override
    public void visit(NumericBind bind) {}

    @Override
    public void visit(KeepExpression aexpr) {}

    @Override
    public void visit(MySQLGroupConcat groupConcat) {}

    @Override
    public void visit(ExpressionList<?> expressionList) {}

    @Override
    public void visit(RowConstructor<?> rowConstructor) {}

    @Override
    public void visit(RowGetExpression rowGetExpression) {}

    @Override
    public void visit(OracleHint hint) {}

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {}

    @Override
    public void visit(DateTimeLiteralExpression literal) {}

    @Override
    public void visit(NotExpression aThis) {}

    @Override
    public void visit(NextValExpression aThis) {}

    @Override
    public void visit(CollateExpression aThis) {}

    @Override
    public void visit(SimilarToExpression aThis) {}

    @Override
    public void visit(ArrayExpression aThis) {}

    @Override
    public void visit(ArrayConstructor aThis) {}

    @Override
    public void visit(VariableAssignment aThis) {}

    @Override
    public void visit(XMLSerializeExpr aThis) {}

    @Override
    public void visit(TimezoneExpression aThis) {}

    @Override
    public void visit(JsonAggregateFunction aThis) {}

    @Override
    public void visit(JsonFunction aThis) {}

    @Override
    public void visit(ConnectByRootOperator aThis) {}

    @Override
    public void visit(OracleNamedFunctionParameter aThis) {}

    @Override
    public void visit(AllColumns allColumns) {}

    @Override
    public void visit(AllTableColumns allTableColumns) {}

    @Override
    public void visit(AllValue allValue) {}

    @Override
    public void visit(IsDistinctExpression isDistinctExpression) {
        visitBinaryExpression(isDistinctExpression);
    }

    @Override
    public void visit(GeometryDistance geometryDistance) {}


    @Override
    public void visit(Select selectBody) {
        selectBody.accept(this.getVisitorManager().getSelectVisitor());
    }

    @Override
    public void visit(TranscodingFunction transcodingFunction) {}

    @Override
    public void visit(TrimFunction trimFunction) {}

    @Override
    public void visit(RangeExpression rangeExpression) {}

    @Override
    public void visit(TSQLLeftJoin tsqlLeftJoin) {}

    @Override
    public void visit(TSQLRightJoin tsqlRightJoin) {}

    @Override
    public VisitorManager getVisitorManager() {
        return this.visitorManager;
    }

    @Override
    public void setVisitorManager(VisitorManager visitorManager) {
        this.visitorManager = visitorManager;
    }

    protected void visitBinaryExpression(BinaryExpression expression) {
        this.setParentNode(expression, ChildPosition.LEFT);
        expression.getLeftExpression().accept(this);

        this.setParentNode(expression, ChildPosition.RIGHT);
        expression.getRightExpression().accept(this);
    }

    public ParentNode getParentNode() {
        return this.parentNode;
    }

    protected void setParentNode(Expression expression, ChildPosition position) {
        this.parentNode.setParent(expression, position);
    }

    protected void setParentNode(ParentNode parentNode) {
        this.parentNode.setParent(parentNode.getParentExpression(), parentNode.getChildPosition());
    }

    protected void setASTNode(Expression expression){
        if (this.parentNode == null) return;

        if (this.parentNode.getChildPosition() == ChildPosition.LEFT){
            if (parentNode.getParentExpression() instanceof BinaryExpression) {
                ((BinaryExpression) parentNode.getParentExpression()).setLeftExpression(expression);
            }
        }
        else {
            if (parentNode.getParentExpression() instanceof BinaryExpression) {
                ((BinaryExpression) parentNode.getParentExpression()).setRightExpression(expression);
            }
            if (parentNode.getParentExpression() instanceof ExistsExpression) {
                ((ExistsExpression) parentNode.getParentExpression()).setRightExpression(expression);
            }
        }
    }
}
