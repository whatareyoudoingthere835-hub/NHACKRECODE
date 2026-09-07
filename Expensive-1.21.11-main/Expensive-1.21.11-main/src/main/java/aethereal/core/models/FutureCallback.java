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

import java.util.concurrent.CompletableFuture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureCallback<T> implements Callback<T> {
    final CompletableFuture future;

    public FutureCallback(CompletableFuture completableFuture) {
        this.future = completableFuture;
    }

    public void onResponse(Call<T> call, Response<T> response) {
        if (!response.isSuccessful() || response.body() == null) {
            this.future.completeExceptionally(CloudConfigService.httpError(response));
        } else {
            this.future.complete(response.body());
        }
    }

    public void onFailure(Call<T> call, Throwable th) {
        this.future.completeExceptionally(th);
    }
}
