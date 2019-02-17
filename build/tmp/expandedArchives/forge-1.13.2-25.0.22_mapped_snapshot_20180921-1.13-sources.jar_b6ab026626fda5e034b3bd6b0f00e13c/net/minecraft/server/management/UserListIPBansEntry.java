package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class UserListIPBansEntry extends UserListEntryBan<String> {
   public UserListIPBansEntry(String valueIn) {
      this(valueIn, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserListIPBansEntry(String valueIn, @Nullable Date startDate, @Nullable String banner, @Nullable Date endDate, @Nullable String banReason) {
      super(valueIn, startDate, banner, endDate, banReason);
   }

   public ITextComponent getDisplayName() {
      return new TextComponentString(this.getValue());
   }

   public UserListIPBansEntry(JsonObject json) {
      super(getIPFromJson(json), json);
   }

   private static String getIPFromJson(JsonObject json) {
      return json.has("ip") ? json.get("ip").getAsString() : null;
   }

   protected void onSerialization(JsonObject data) {
      if (this.getValue() != null) {
         data.addProperty("ip", this.getValue());
         super.onSerialization(data);
      }
   }
}