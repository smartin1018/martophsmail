package com.rowlingsrealm.owlery.mail;

import com.rowlingsrealm.owlery.C;
import com.rowlingsrealm.owlery.Lang;
import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.util.UtilInv;
import com.rowlingsrealm.owlery.util.UtilString;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MailItem {

    private boolean read;
    private long timeSentMillis;
    private UUID sender;
    private UUID receiver;
    private String message;
    private String hex;
    private List<ItemStack> itemStacks = new ArrayList<>();

    public MailItem() {
    }

    public MailItem(UUID sender) {

        setSender(sender);

        new AnvilGUI(Owlery.getPlugin(), Bukkit.getPlayer(sender), C.Gray + "Player name...", (player, reply) -> {
            Player player1 = Bukkit.getPlayer(reply);

            if (reply.equals(player.getDisplayName()) && !player.hasPermission("owlery.sendtoself")) {
                player.closeInventory();
                player.sendMessage(Lang.getProperty("same-player"));
                return "Same player";
            }

            if (player1 == null) {
                player.closeInventory();
                player.sendMessage(Lang.getProperty("invalid-player"));
                return "Invalid player";
            }

            setReceiver(player1.getUniqueId());
            player.sendMessage(Lang.getProperty("enter-message", "{PLAYER}", player1.getName()));
            new MailCreator(this);
            player.closeInventory();

            return "Complete.";
        });

    }

    public MailItem(UUID sender, UUID receiver, String message, List<ItemStack> itemStacks) {
        read = false;
        timeSentMillis = System.currentTimeMillis();
        hex = String.format("#%06x", new Random().nextInt((int) Math.pow(256, 3)));
        this.sender = sender;
        this.message = message;
        this.itemStacks = itemStacks;

        Owlery.getCentralManager().getMailManager().addMessage(receiver, this);
    }

    @SuppressWarnings("unchecked")
    public MailItem(String json, UUID receiver) throws ParseException {
        this.receiver = receiver;
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);

        Iterator<String> keysItr = (Iterator<String>) jsonObject.keySet().iterator();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            String value = jsonObject.get(key).toString();

            switch (key) {
                case "sender":
                    sender = UUID.fromString(value);
                    break;
                case "message":
                    message = value;
                    break;
                case "time-sent":
                    timeSentMillis = Long.parseLong(value);
                    break;
                case "items":
                    itemStacks = getItemsFromJSON(value);
                    break;
                case "read":
                    read = Boolean.parseBoolean(value);
                    break;
                case "hex":
                    hex = value;
                    break;
            }

        }

    }

    public String getJSONString() {
        HashMap<String, String> mailMap = new HashMap<String, String>() {{
            put("sender", getSender().toString());
            put("message", getMessage());
            put("time-sent", getTimeSentMillis() + "");
            put("items", getItemArrayJSON());
            put("read", wasRead() + "");
            put("hex", getHex());
        }};

        return new JSONObject(mailMap).toJSONString();
    }

    public void open(Player player) {

        Inventory inventory = UtilInv.surroundInventory(Bukkit.createInventory(null, 54, Bukkit.getOfflinePlayer(sender).getName()), new ItemStack(Material.STAINED_GLASS_PANE));
        inventory.setItem(4, UtilInv.createBook(this));
        itemStacks.forEach(inventory::addItem);

        player.openInventory(inventory);

    }

    public MailItem parse(String hex) {
        HashMap<UUID, List<MailItem>> messageMap = Owlery.getCentralManager().getMailManager().getMessageMap();
        List<MailItem> messages = new ArrayList<>();

        messageMap.values().forEach(messages::addAll);

        for (MailItem mailItem : messages) {
            if (mailItem.getHex().equals(hex))
                return mailItem;
        }

        return null;

    }

    private String getItemArrayJSON() {
        List<String> jsonObjects = new ArrayList<>();

        for (ItemStack itemStack : getItemStacks()) {
            jsonObjects.add(UtilInv.getJSONStringFromItem(itemStack));
        }

        return UtilString.join(jsonObjects.toArray(new String[jsonObjects.size()]), -1, ", ");
    }

    private List<ItemStack> getItemsFromJSON(String jsonString) throws ParseException {

        List<String> jsonArray = new ArrayList<>(Arrays.asList(jsonString.split(", ")));
        ArrayList<ItemStack> itemStacks = new ArrayList<>();

        for (String string : jsonArray) itemStacks.add(UtilInv.getItemFromJsonString(string));

        return itemStacks;
    }

    public ItemStack toItemStack() {

        DateFormat parseFormat = new SimpleDateFormat(
                Objects.requireNonNull(Lang.getProperty("date-format")));

        return read ? UtilInv.createItem(Material.BOOK, C.White + "Message", new String[]{
                C.Gray + "From: " + C.Red + Bukkit.getOfflinePlayer(sender).getName(), C.Gray + "Read", C.Red + parseFormat.format(new Date(timeSentMillis)), C.Gray + getHex(), C.Gray + "R-Click to delete."
        }, 1) : UtilInv.addGlow(UtilInv.createItem(Material.BOOK, C.White + "Message", new String[]{
                C.Gray + "From: " + C.Red + Bukkit.getOfflinePlayer(sender).getName(), C.White + "Unread", C.Red + parseFormat.format(new Date(timeSentMillis)), C.Gray + getHex()
        }, 1));
    }

    public UUID getReceiver() {
        return receiver;
    }

    private void setReceiver(UUID receiver) {
        this.receiver = receiver;
    }

    private void setSender(UUID sender) {
        this.sender = sender;
    }

    private boolean wasRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    private long getTimeSentMillis() {
        return timeSentMillis;
    }

    public UUID getSender() {
        return sender;
    }

    public String getHex() {
        return hex;
    }

    public String getMessage() {
        return message;
    }

    private List<ItemStack> getItemStacks() {
        return itemStacks;
    }

    public void setItemStacks(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }
}
