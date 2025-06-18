import {Client} from '@stomp/stompjs';

export const stompClient = new Client({
    brokerURL: import.meta.env.VITE_WS_URL ?? 'ws://localhost:8080/ws',
    reconnectDelay: 5000,
});

export const connectWS = () => {
    if (!stompClient.active) stompClient.activate();
};