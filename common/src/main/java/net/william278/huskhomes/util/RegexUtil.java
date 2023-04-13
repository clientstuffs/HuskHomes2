/*
 * This file is part of HuskHomes, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskhomes.util;

import net.william278.huskhomes.position.PositionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Provides a number of utility regular expressions for checking home name patterns
 */
public final class RegexUtil {

    /**
     * Pattern for checking home and warp {@link PositionMeta} description fields
     */
    public static final Pattern DESCRIPTION_PATTERN = Pattern.compile("[a-zA-Z\\d\\-_\\s]*");

    /**
     * Pattern for checking home and warp {@link PositionMeta} name fields
     */
    public static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z\\d_\\-]+");

    /**
     * Pattern for checking home input fields disambiguated by the owner's name
     * <p>
     * e.g. {@code ownerName.homeName}
     */
    private static final Pattern OWNER_DISAMBIGUATED_HOME_IDENTIFIER_PATTERN = Pattern.compile("\\w+\\.[^.]{1,16}$");

    /**
     * Match pattern for checking home input fields disambiguated by the owner's name
     *
     * @param input input string to match
     * @return An optional containing the {@link DisambiguatedHomeIdentifier} if matched, otherwise empty if not
     */
    public static Optional<DisambiguatedHomeIdentifier> matchDisambiguatedHomeIdentifier(@NotNull String input) {
        if (OWNER_DISAMBIGUATED_HOME_IDENTIFIER_PATTERN.matcher(input).matches()) {
            final String[] separatedInput = input.split(Pattern.quote("."));
            return Optional.of(new DisambiguatedHomeIdentifier(separatedInput[0], separatedInput[1]));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Represents an identifier for a home or warp {@link PositionMeta} that is disambiguated by the owner's name
     */
    public static final class DisambiguatedHomeIdentifier {

        /**
         * the username of a home's owner
         */
        private final String ownerName;

        /**
         * the name of a home
         */
        private final String homeName;

        public DisambiguatedHomeIdentifier(String ownerName, String homeName) {
            this.ownerName = ownerName;
            this.homeName = homeName;
        }

        public String ownerName() {
            return ownerName;
        }

        public String homeName() {
            return homeName;
        }

        /**
         * Get the period-separated formatted disambiguated home identifier
         *
         * @return the formatted disambiguated home identifier (e.g. {@code ownerName.homeName})
         */
        @Override
        public String toString() {
            return ownerName + "." + homeName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DisambiguatedHomeIdentifier that = (DisambiguatedHomeIdentifier) o;

            if (!Objects.equals(ownerName, that.ownerName)) return false;
            return Objects.equals(homeName, that.homeName);
        }

        @Override
        public int hashCode() {
            int result = ownerName != null ? ownerName.hashCode() : 0;
            result = 31 * result + (homeName != null ? homeName.hashCode() : 0);
            return result;
        }
    }

}
