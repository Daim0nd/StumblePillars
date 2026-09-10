package com.stumblePillars;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;


public class ReloadHandler {

    public void handle(CommandSender sender){
        sender.showDialog(getDialog());
    }

    private Dialog getDialog(){

        DialogAction yesAction = DialogAction.staticAction(ClickEvent.runCommand("sp reload --force"));
        ActionButton yes = ActionButton.builder(Component.text("Sim")).action(yesAction).build();
        ActionButton no = ActionButton.builder(Component.text("Não")).action(null).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Gostaria de dar reload?")).build())
                .type(DialogType.confirmation(yes,no))
        );
    }

}
