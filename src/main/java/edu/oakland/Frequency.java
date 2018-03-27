package edu.oakland;

public enum Frequency {
    NEVER {
        @Override
        public String toString() {
            return "Never";
        }
    },
    DAILY {
        @Override
        public String toString() {
            return "Daily";
        }
    },
    WEEKLY {
        @Override
        public String toString() {
            return "Weekly";
        }
    },
    MONTHLY {
        @Override
        public String toString() {
            return "Monthly";
        }
    }
}
