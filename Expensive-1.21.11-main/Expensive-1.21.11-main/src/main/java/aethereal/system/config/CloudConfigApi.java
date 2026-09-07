package aethereal.system.config;
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

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CloudConfigApi {
    @GET("configs/")
    public Call<List<CloudConfigMetadata>> listConfigs();

    @GET("configs/{id}")
    public Call<CloudConfigDto> fetchConfig(@Path("id") String str);

    @POST("configs/")
    public Call<ConfigIdStub> createConfig(@Body CreateConfigRequest class366Var);

    @PUT("configs/")
    public Call<Void> updateConfig(@Body UpdateConfigRequest class364Var);

    @PATCH("configs/")
    public Call<Void> renameConfig(@Body RenameConfigRequest class367Var);

    @HTTP(method = "DELETE", path = "configs/", hasBody = true)
    public Call<Void> deleteConfig(@Body DeleteConfigRequest class368Var);

    @POST("configs/import")
    public Call<Void> importConfig(@Body ImportConfigRequest class365Var);
}
