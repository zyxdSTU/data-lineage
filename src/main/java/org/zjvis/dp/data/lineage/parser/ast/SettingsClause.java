package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.SettingExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SettingsClause extends INode {

    private List<SettingExpr> settingExprs;

    public SettingsClause(List<SettingExpr> settingExprs) {
        this.settingExprs = settingExprs;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitSettingsClause(this);
    }

}
