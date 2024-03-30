package ro.settings;

public enum RepeatMode
{
    OFF(null, "Off"),
    ALL("\uD83D\uDD01", "All"), // 🔁
    SINGLE("\uD83D\uDD02", "Single"); // 🔂

    private final String emoji;
    private final String userFriendlyName;

    private RepeatMode(String emoji, String userFriendlyName)
    {
        this.emoji = emoji;
        this.userFriendlyName = userFriendlyName;
    }

    public String getEmoji()
    {
        return emoji;
    }

    public String getUserFriendlyName()
    {
        return userFriendlyName;
    }
}