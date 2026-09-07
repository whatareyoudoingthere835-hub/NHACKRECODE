package aethereal.core.models;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;


public final class SearchResultRow {
    public final SearchMatch searchResult;
    public final Translation qualifier;

    public final SearchTypeBadge typeLabel;

    public SearchResultRow(SearchMatch class794Var, Translation class254Var, SearchTypeBadge class791Var) {
        this.searchResult = class794Var;
        this.qualifier = class254Var;
        this.typeLabel = class791Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "searchResult=" + this.searchResult + ", " + "qualifier=" + this.qualifier + ", " + "typeLabel=" + this.typeLabel + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.searchResult, this.qualifier, this.typeLabel);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SearchResultRow)) return false;
        SearchResultRow o= (SearchResultRow) obj;
        return java.util.Objects.equals(this.searchResult, o.searchResult) && java.util.Objects.equals(this.qualifier, o.qualifier) && java.util.Objects.equals(this.typeLabel, o.typeLabel);
    }
public SearchMatch searchResult() {
        return this.searchResult;
    }

    public Translation qualifier() {
        return this.qualifier;
    }

    public SearchTypeBadge typeLabel() {
        return this.typeLabel;
    }
}
