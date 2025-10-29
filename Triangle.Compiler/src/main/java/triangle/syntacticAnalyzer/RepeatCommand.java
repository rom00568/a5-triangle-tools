package triangle.syntacticAnalyzer;

import triangle.abstractSyntaxTrees.commands.Command;
import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.codeGenerator.Frame;

public class RepeatCommand extends Command{
    public RepeatCommand(Expression eAST, Command cAST, SourcePosition position) {
        super(position);
        E = eAST;
        C = cAST;
    }

    public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
        return v.visitRepeatCommand(this, arg);
    }

    public Expression E;
    public final Command C;
}
