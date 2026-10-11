package com.sukimod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Suki: mob bulat berkacamata.
 *  - Liar: kasih COOKIE buat jinakin (peluang 1/3 per cookie)
 *  - Jinak: ngelindungin owner kayak wolf
 *  - Klik kanan pegang EMERALD  -> dapet 1 DIAMOND (1:1)
 *  - Shift + klik kanan         -> duduk / berdiri
 *  - Klik kanan pegang COOKIE   -> heal Suki
 */
public class SukiEntity extends TamableAnimal {

    public SukiEntity(EntityType<? extends SukiEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // ===== Belum jinak: kasih cookie =====
        if (!this.isTame()) {
            if (stack.is(Items.COOKIE)) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    stack.consume(1, player);
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
                        this.setHealth(30.0f);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(true);
                        serverLevel.broadcastEntityEvent(this, (byte) 7); // hati
                    } else {
                        serverLevel.broadcastEntityEvent(this, (byte) 6); // asap
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // ===== Sudah jinak & lo owner-nya =====
        if (this.isOwnedBy(player)) {
            // Shift + klik kanan = duduk / berdiri
            if (player.isShiftKeyDown()) {
                if (this.level() instanceof ServerLevel) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.navigation.stop();
                    this.setTarget(null);
                }
                return InteractionResult.SUCCESS;
            }

            // TRADING: 1 emerald -> 1 diamond
            if (stack.is(Items.EMERALD)) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    stack.consume(1, player);
                    ItemStack diamond = new ItemStack(Items.DIAMOND);
                    if (!player.getInventory().add(diamond)) {
                        player.drop(diamond, false);
                    }
                    this.playSound(SoundEvents.VILLAGER_YES, 1.0f, 1.3f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 0.8, this.getZ(), 8, 0.3, 0.3, 0.3, 0.0);
                }
                return InteractionResult.SUCCESS;
            }

            // Heal pakai cookie
            if (stack.is(Items.COOKIE) && this.getHealth() < this.getMaxHealth()) {
                if (this.level() instanceof ServerLevel) {
                    stack.consume(1, player);
                    this.heal(4.0f);
                }
                return InteractionResult.SUCCESS;
            }

            // Tangan kosong / item lain: kasih petunjuk
            if (this.level() instanceof ServerLevel) {
                player.displayClientMessage(Component.translatable("message.sukimod.suki.hint"), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    // ===== Biar nggak bisa breeding =====
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COOKIE);
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // ===== Suara (pinjem suara villager dulu) =====
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
}
