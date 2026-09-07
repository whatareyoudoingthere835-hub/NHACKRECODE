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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModuleProvider {
    public List<Supplier<Module>> getAll() {
        ArrayList arrayList= new ArrayList();
        arrayList.addAll(combatModules());
        arrayList.addAll(movementModules());
        arrayList.addAll(renderModules());
        arrayList.addAll(playerModules());
        arrayList.addAll(miscModules());
        arrayList.addAll(earningsModules());
        arrayList.addAll(autoBuyModules());
        return arrayList;
    }

    public List<Supplier<Module>> autoBuyModules() {
        return List.of(new Supplier[]{
            withCategory(AutoBuyModule::new, ModuleCategory.AUTOMATION)
        });
    }

    public List<Supplier<Module>> earningsModules() {
        return List.of(new Supplier[]{
            withCategory(AppleFarmerModule::new, ModuleCategory.AUTOMATION),
            withCategory(AutoWardenModule::new, ModuleCategory.AUTOMATION),
            withCategory(AutoMineModule::new, ModuleCategory.AUTOMATION),
            withCategory(AutoTradeModule::new, ModuleCategory.AUTOMATION),
            withCategory(AutoCrafterModule::new, ModuleCategory.AUTOMATION)
        });
    }

    public List<Supplier<Module>> combatModules() {
        return List.of(new Supplier[]{withCategory(AntiBotModule::new, ModuleCategory.DEFENSE), withCategory(AutoGAppleModule::new, ModuleCategory.AUTOMATION), withCategory(AutoSwapModule::new, ModuleCategory.AUTOMATION), withCategory(AutoTotemModule::new, ModuleCategory.DEFENSE), withCategory(HitBoxesModule::new, ModuleCategory.ATTACK), withCategory(NoFriendDamageModule::new, ModuleCategory.DEFENSE), withCategory(NoPlayerTraceModule::new, ModuleCategory.CONVENIENCE), withCategory(TriggerBotModule::new, ModuleCategory.ATTACK), withCategory(FastBowModule::new, ModuleCategory.AUTOMATION), withCategory(AutoExplosionModule::new, ModuleCategory.AUTOMATION), withCategory(CriticalsModule::new, ModuleCategory.ATTACK), withCategory(VelocityModule::new, ModuleCategory.ATTACK), withCategory(AttackAuraModule::new, ModuleCategory.ATTACK), withCategory(TargetPearlModule::new, ModuleCategory.AUTOMATION), withCategory(AutoWebModule::new, ModuleCategory.AUTOMATION)});
    }

    public List<Supplier<Module>> movementModules() {
        return List.of(new Supplier[]{withCategory(SprintModule::new, ModuleCategory.AUTOMATION), withCategory(WaterSpeedModule::new, ModuleCategory.BOOST), withCategory(NoFallModule::new, ModuleCategory.EXPLOITS), withCategory(AutoJumpModule::new, ModuleCategory.AUTOMATION), withCategory(EdgeJumpModule::new, ModuleCategory.AUTOMATION), withCategory(FlightModule::new, ModuleCategory.EXPLOITS), withCategory(ElytraFlyModule::new, ModuleCategory.BOOST), withCategory(SpiderModule::new, ModuleCategory.AUTOMATION), withCategory(SpeedModule::new, ModuleCategory.BOOST), withCategory(NoWebModule::new, ModuleCategory.ANTI_LIMITS), withCategory(ElytraBoosterModule::new, ModuleCategory.BOOST), withCategory(ElytraRecastModule::new, ModuleCategory.AUTOMATION), withCategory(NoSlowModule::new, ModuleCategory.ANTI_LIMITS), withCategory(NoJumpDelayModule::new, ModuleCategory.ANTI_LIMITS), withCategory(ScreenWalkModule::new, ModuleCategory.ANTI_LIMITS)});
    }

    public List<Supplier<Module>> renderModules() {
        return List.of(new Supplier[]{withCategory(SkeletonModule::new, ModuleCategory.VISUALIZATION), withCategory(TargetESPModule::new, ModuleCategory.VISUALIZATION), withCategory(RemovalsModule::new, ModuleCategory.WORLD), withCategory(AspectRatioModule::new, ModuleCategory.INTERFACE), withCategory(FullBrightModule::new, ModuleCategory.WORLD), withCategory(ArrowsModule::new, ModuleCategory.VISUALIZATION), withCategory(PopChamsModule::new, ModuleCategory.VISUALIZATION), withCategory(JumpCircleModule::new, ModuleCategory.VISUALIZATION), withCategory(ChamsModule::new, ModuleCategory.VISUALIZATION), withCategory(WidgetsModule::new, ModuleCategory.INTERFACE), withCategory(ESPModule::new, ModuleCategory.VISUALIZATION), withCategory(ContainerESPModule::new, ModuleCategory.VISUALIZATION), withCategory(WorldTweaksModule::new, ModuleCategory.WORLD), withCategory(ArmTweaksModule::new, ModuleCategory.VISUALIZATION), withCategory(CrosshairModule::new, ModuleCategory.INTERFACE), withCategory(CameraTweaksModule::new, ModuleCategory.INTERFACE), withCategory(ParticlesModule::new, ModuleCategory.VISUALIZATION), withCategory(ProjectilePredictionModule::new, ModuleCategory.VISUALIZATION)});
    }

    public List<Supplier<Module>> playerModules() {
        return List.of(new Supplier[]{withCategory(ClickPearlModule::new, ModuleCategory.CONVENIENCE), withCategory(ItemScrollerModule::new, ModuleCategory.CONVENIENCE), withCategory(RemoveEffectsModule::new, ModuleCategory.CONVENIENCE), withCategory(ContainerStealerModule::new, ModuleCategory.AUTOMATION), withCategory(FastUseModule::new, ModuleCategory.AUTOMATION), withCategory(AutoPotionModule::new, ModuleCategory.AUTOMATION), withCategory(DeathCoordinatesModule::new, ModuleCategory.CONVENIENCE), withCategory(AutoRespawnModule::new, ModuleCategory.AUTOMATION), withCategory(FastBreakModule::new, ModuleCategory.ANTI_LIMITS), withCategory(AutoToolModule::new, ModuleCategory.AUTOMATION), withCategory(AutoEatModule::new, ModuleCategory.AUTOMATION), withCategory(AutoFishModule::new, ModuleCategory.AUTOMATION), withCategory(FreeLookModule::new, ModuleCategory.CONVENIENCE), withCategory(NoServerRotationModule::new, ModuleCategory.ANTI_LIMITS), withCategory(NoPushModule::new, ModuleCategory.ANTI_LIMITS), withCategory(FreeCameraModule::new, ModuleCategory.CONVENIENCE), withCategory(AutoArmorModule::new, ModuleCategory.AUTOMATION), withCategory(NoInteractModule::new, ModuleCategory.ANTI_LIMITS), withCategory(NukerModule::new, ModuleCategory.AUTOMATION), withCategory(AutoMystModule::new, ModuleCategory.AUTOMATION)});
    }

    public List<Supplier<Module>> miscModules() {
        return List.of(new Supplier[]{withCategory(AntiAFKModule::new, ModuleCategory.AUTOMATION), withCategory(AutoLeaveModule::new, ModuleCategory.AUTOMATION), withCategory(ItemSwapFixModule::new, ModuleCategory.UTILITIES), withCategory(AntiServerRPModule::new, ModuleCategory.UTILITIES), withCategory(TPAcceptModule::new, ModuleCategory.AUTOMATION), withCategory(ClickFriendModule::new, ModuleCategory.CONVENIENCE), withCategory(AuctionRelistModule::new, ModuleCategory.AUTOMATION), withCategory(AutoAuthModule::new, ModuleCategory.AUTOMATION), withCategory(BetterChatModule::new, ModuleCategory.CONVENIENCE), withCategory(NameProtectModule::new, ModuleCategory.UTILITIES), withCategory(MultiActionsModule::new, ModuleCategory.UTILITIES), withCategory(TapeMouseModule::new, ModuleCategory.UTILITIES), withCategory(ItemTrackerModule::new, ModuleCategory.UTILITIES), withCategory(ElytraHelperModule::new, ModuleCategory.UTILITIES), withCategory(FTHelperModule::new, ModuleCategory.UTILITIES), withCategory(RWHelperModule::new, ModuleCategory.UTILITIES), withCategory(HWHelperModule::new, ModuleCategory.UTILITIES), withCategory(SoundsModule::new, ModuleCategory.CONVENIENCE), withCategory(RWGriefJoinerModule::new, ModuleCategory.AUTOMATION), withCategory(AncientXRayModule::new, ModuleCategory.EXPLOITS), withCategory(HitSoundsModule::new, ModuleCategory.CONVENIENCE), withCategory(BlinkModule::new, ModuleCategory.EXPLOITS), withCategory(AuctionHelperModule::new, ModuleCategory.UTILITIES), withCategory(EnderChestPlusModule::new, ModuleCategory.CONVENIENCE), withCategory(AutoTpLootModule::new, ModuleCategory.AUTOMATION), withCategory(SeeInvisiblesModule::new, ModuleCategory.UTILITIES), withCategory(ClanInvestModule::new, ModuleCategory.AUTOMATION), withCategory(AutoDuelModule::new, ModuleCategory.AUTOMATION), withCategory(MineHelperModule::new, ModuleCategory.UTILITIES), withCategory(PvPSafeModule::new, ModuleCategory.UTILITIES), withCategory(InventoryPlusModule::new, ModuleCategory.CONVENIENCE)});
    }

    public Supplier<Module> withCategory(Supplier<Module> supplier, ModuleCategory class672Var) {
        return () -> {
            Module class605Var= (Module) supplier.get();
            class605Var.setCategory(class672Var);
            return class605Var;
        };
    }
}
