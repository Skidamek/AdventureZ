package net.adventurez.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.adventurez.init.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    private int repairedAmount;

    public SmithingScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void updateResultMixin(CallbackInfo info) {
        ItemStack itemStack = this.input.getStack(0);
        if (itemStack.getItem() == ItemInit.PRIME_EYE && itemStack.getDamage() != 0) {
            ItemStack itemStack2 = this.input.getStack(1);
            if (itemStack2.getItem() == Items.ENDER_PEARL) {
                ItemStack itemStack3 = new ItemStack(ItemInit.PRIME_EYE);
                repairedAmount = itemStack2.getCount() > itemStack.getDamage() ? itemStack.getDamage() : itemStack2.getCount();
                itemStack3.setDamage(itemStack.getDamage() - repairedAmount);
                this.output.setStack(0, itemStack3);
                info.cancel();
            }
        }
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    public void canTakeOutputMixin(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> info) {
        ItemStack itemStack = this.input.getStack(0);
        if (itemStack.getItem() == ItemInit.PRIME_EYE && itemStack.getDamage() != 0) {
            ItemStack itemStack2 = this.input.getStack(1);
            if (itemStack2.getItem() == Items.ENDER_PEARL) {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    public void onTakeOutputMixin(PlayerEntity player, ItemStack stack, CallbackInfo info) {
        ItemStack itemStack = this.input.getStack(0);
        if (itemStack.getItem() == ItemInit.PRIME_EYE) {
            ItemStack itemStack2 = this.input.getStack(1);
            if (itemStack2.getItem() == Items.ENDER_PEARL) {
                itemStack2.decrement(repairedAmount - 1);
                this.input.setStack(1, itemStack2);
            }
        }
    }

}
