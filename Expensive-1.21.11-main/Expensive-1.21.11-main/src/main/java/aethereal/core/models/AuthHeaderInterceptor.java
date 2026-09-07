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

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public class AuthHeaderInterceptor implements Interceptor {
    public final AccountProfile accountProfile;

    public AuthHeaderInterceptor(AccountProfile class013Var) {
        this.accountProfile = class013Var;
    }

    @NotNull
    public Response intercept(Interceptor.Chain chain) throws java.io.IOException {
        Request.Builder builderHeader = chain.request().newBuilder().header("X-Uid", this.accountProfile.uid()).header("X-Hwid", this.accountProfile.hwid()).header("X-Login", this.accountProfile.login()).header("Authorization", this.accountProfile.signature());
        if (this.accountProfile.avatarUrl() != null) {
            builderHeader.header("X-Avatar-Url", this.accountProfile.avatarUrl());
        }
        return chain.proceed(builderHeader.build());
    }
}
