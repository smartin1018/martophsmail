package com.rowlingsrealm.owlery.util;

import com.rowlingsrealm.owlery.anvil.NMSManager;
import com.rowlingsrealm.owlery.mail.MailItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Method;
import java.util.*;

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
        try {
            Class<?> CraftItemStack = NMSManager.get().getNMSClass(".inventory.CraftItemStack");
            Class<?> NBTTagCompound = NMSManager.get().getNMSClass("NBTTagCompound");
            Class<?> NBTTagList = NMSManager.get().getNMSClass("NBTTagList");

            Object nmsStack = NMSManager.get().invokeMethodWithArgs("asNMSCopy", CraftItemStack, itemStack);
            Object nbtTagCompound = null;

            if (NMSManager.get().invokeMethod("hasTag", nmsStack).equals(false)) {
                nbtTagCompound = NBTTagCompound.getConstructor();
                NMSManager.get().invokeMethodWithArgs("setTag", nmsStack, nbtTagCompound);
            }

            if (nbtTagCompound == null)
                nbtTagCompound = NMSManager.get().invokeMethod("getTag()", nmsStack);

            Object ench = NBTTagList.getConstructor();
            NMSManager.get().invokeMethodWithArgs("set", nbtTagCompound, "ench", ench);
            NMSManager.get().invokeMethodWithArgs("setTag", nmsStack, nbtTagCompound);

            return (ItemStack) NMSManager.get().invokeMethodWithArgs("asCraftMirror", CraftItemStack, nmsStack);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ItemStack createBook(MailItem mailItem) {
        ItemStack book = new ItemStack(Material.BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        bookMeta.setTitle(Bukkit.getOfflinePlayer(mailItem.getSender()).getName());
        bookMeta.setAuthor(mailItem.getHex());

        for (int i = 0; i < Math.ceil(mailItem.getMessage().length() / 256); i++) {
            bookMeta.addPage(mailItem.getMessage().substring(i * 256));
        }

        book.setItemMeta(bookMeta);

        return book;

    }


    public static String getJSONStringFromItem(ItemStack itemStack) {

        if (itemStack == null)
            return null;

        HashMap<String, Object> jsonMap = new HashMap<>();

        itemStack.serialize().entrySet().forEach(entry -> jsonMap.put(entry.getKey(), entry.getValue()));

        if (jsonMap.containsKey("meta")) {

            JSONObject metaObject = new JSONObject(itemStack.getItemMeta().serialize());
            jsonMap.put("meta", metaObject.toJSONString());

        }


        //UnleashedCore.sendMessage(new JSONObject(jsonMap).toJSONString().replace("\\", "\\\\"));

        return new JSONObject(jsonMap).toJSONString().replace("\\", "\\\\");
    }

    @SuppressWarnings("unchecked")
    public static ItemStack getItemFromJsonString(String json) throws ParseException {

        if (json.isEmpty())
            return null;

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(json);

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


    public static ItemMeta deserializeItemMeta(Map<String, Object> map) {

        ItemMeta meta = null;

        try {
            Class[] craftMetaItemClasses = Class.forName("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaItem").getDeclaredClasses();

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

}
