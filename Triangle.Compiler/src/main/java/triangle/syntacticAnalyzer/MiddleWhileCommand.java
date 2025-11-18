package triangle.syntacticAnalyzer;

import triangle.abstractSyntaxTrees.commands.Command;
import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;

public class MiddleWhileCommand extends Command{
    public MiddleWhileCommand(Expression eAST, Command cAST, Command regardlessAST, SourcePosition position) {
        super(position);
        E = eAST;
        C = cAST;
        R = regardlessAST;
    }

    public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
        return v.visitMiddleWhileCommand(this, arg);
    }

    public Expression E;
    public Command R;
    public final Command C;
}

