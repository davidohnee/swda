<script setup lang="ts">
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import { useNotificationStore } from "@/stores/notifications";

    const notifications = useNotificationStore();
</script>
<template>
    <div class="notifications">
        <h1>Notifications</h1>
        <ErrorLoader :content="notifications.response">
            <div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Message</th>
                            <th>Recipient</th>
                            <th>Sent</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="item in notifications.notifications"
                            :key="item.id"
                        >
                            <td>
                                {{ item.id }}
                            </td>
                            <td>
                                {{ item.message }}
                            </td>
                            <td>
                                <em class="muted">
                                    {{ item.recipient.type }}
                                </em>
                                <br />
                                {{ item.recipient.id }}
                            </td>
                            <td>
                                {{ item.sent.toLocaleString() }}
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p
                    class="muted center"
                    v-if="notifications.notifications.length === 0"
                >
                    <em> No notifications found. </em>
                </p>
            </div>
        </ErrorLoader>
    </div>
</template>
