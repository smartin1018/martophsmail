package com.rowlingsrealm.owlery.util;

import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.mail.MailItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
public class UtilInv {

    public static Inventory surroundInventory(Inventory inventory, ItemStack itemStack) {
        int size = inventory.getSize();

        if (size <= 18)
            return inventory;

        for (int i = 0; i < size; i++) {
            if (i <= 9 || i >= size - 9)
                inventory.setItem(i, itemStack);

            if (i % 9 == 0 || i % 9 == 8)
                inventory.setItem(i, itemStack);
        }

        return inventory;
    }

    public static ItemStack createItem(Material material, String name, String[] lore, int amount) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(amount);

        return itemStack;
    }

    public static ItemStack addGlow(ItemStack itemStack) {

        itemStack.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 0);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createBook(MailItem mailItem) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        bookMeta.setTitle(Bukkit.getOfflinePlayer(mailItem.getSender()).getName());
        bookMeta.setAuthor(mailItem.getHex());

        for (int i = 0; i < Math.ceil(mailItem.getMessage().length() / 256d); i++) {
            bookMeta.addPage(mailItem.getMessage().substring(i * 256));
        }

        book.setItemMeta(bookMeta);

        return book;

    }


    public static String getJSONStringFromItem(ItemStack itemStack) {

        if (itemStack == null)
            return null;

        HashMap<String, Object> jsonMap = new HashMap<>();

        itemStack.serialize().forEach(jsonMap::put);

        if (jsonMap.containsKey("meta")) {

            JSONObject metaObject = new JSONObject(itemStack.getItemMeta().serialize());
            jsonMap.put("meta", metaObject.toJSONString());

        }


        //UnleashedCore.sendMessage(new JSONObject(jsonMap).toJSONString().replace("\\", "\\\\"));

        return new JSONObject(jsonMap).toJSONString().replace("\\", "\\\\");
    }

    public static ItemStack getItemFromJsonString(String json) throws ParseException {

        if (json.isEmpty())
            return null;

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(json);

        if (jsonObject == null)
            return null;

        HashMap<String, Object> jsonMap = new HashMap<>();
        Iterator<String> keysItr = (Iterator<String>) jsonObject.keySet().iterator();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = jsonObject.get(key);

            if (key.equals("meta")) {
                JSONParser metaParser = new JSONParser();
                JSONObject metaObject = (JSONObject) metaParser.parse((String) value);
                HashMap<String, Object> metaMap = new HashMap<>();

                Iterator<String> metaItr = (Iterator<String>) metaObject.keySet().iterator();
                while (metaItr.hasNext()) {
                    String metaKey = metaItr.next();
                    Object metaValue = metaObject.get(metaKey);

                    metaMap.put(metaKey, metaValue);
                }

                value = deserializeItemMeta(metaMap);

            }

            jsonMap.put(key, value);
        }


        return ItemStack.deserialize(jsonMap);
    }

    private static ItemMeta deserializeItemMeta(Map<String, Object> map) {

        ItemMeta meta = null;

        try {
            Class[] craftMetaItemClasses = Class.forName("org.bukkit.craftbukkit." + Owlery.getVersion() + "inventory.CraftMetaItem").getDeclaredClasses();

            for (Class craftMetaItemClass : craftMetaItemClasses) {
                if (!craftMetaItemClass.getSimpleName().equals("SerializableMeta"))
                    continue;

                Method deserialize = craftMetaItemClass.getMethod("deserialize", Map.class);
                meta = (ItemMeta) deserialize.invoke(null, map);

                if (map.containsKey("enchants")) {

                    String string = map.get("enchants").toString().replace("{", "").replace("}", "").replace("\"", "");

                    for (String s : string.split(",")) {
                        String[] split = s.split(":");
                        Enchantment enchantment = Enchantment.getByName(split[0]);
                        int level = Integer.parseInt(split[1]);
                        meta.addEnchant(enchantment, level, true);
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return meta;
    }

    public static void attemptAddToInv(ItemStack itemStack, Player player) {

        if (itemStack == null)
            return;

        HashMap<Integer, ItemStack> hashMap = player.getInventory().addItem(itemStack);

        if (!hashMap.isEmpty()) {
            World world = player.getWorld();

            world.dropItem(player.getLocation(), itemStack);
        }
    }

}
