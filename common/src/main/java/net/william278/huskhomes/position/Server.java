package net.william278.huskhomes.position;

import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Represents a server on a proxied network
 */
@YamlFile(header = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                   "┃ Server ID cache. Must match  ┃\n" +
                   "┃ server name in proxy config. ┃\n" +
                   "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛")
public class Server {

    /**
     * Default (unknown) server identifier
     */
    public static String getDefaultServerName() {
        try {
            final Path serverDirectory = Path.of(System.getProperty("user.dir"));
            return serverDirectory.getFileName().toString().trim();
        } catch (Exception e) {
            return "server";
        }
    }

    /**
     * Proxy-defined name of this server
     */
    @YamlKey("server_name")
    public String name = getDefaultServerName();

    public Server(@NotNull String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    private Server() {
    }

    @Override
    public boolean equals(@NotNull Object other) {
        // If the name of this server matches another, the servers are the same.
        if (other instanceof Server) {
            final var server = (Server) other;

            return server.name.equalsIgnoreCase(this.name);
        }
        return super.equals(other);
    }

}
