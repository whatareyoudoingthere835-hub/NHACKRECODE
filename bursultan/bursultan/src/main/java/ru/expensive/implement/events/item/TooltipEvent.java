package ru.expensive.implement.events.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ru.expensive.api.event.events.Event;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TooltipEvent implements Event {
    ItemStack stack;
    List<Text> lines;
}
