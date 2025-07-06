class WebSocketService {
  private ws: WebSocket | null = null;
  private messageHandlers: ((data: any) => void)[] = [];
  private errorHandlers: ((error: any) => void)[] = [];
  private connectHandlers: (() => void)[] = [];
  private closeHandlers: (() => void)[] = [];

  connect() {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      return;
    }

    this.ws = new WebSocket('ws://localhost:8083');

    this.ws.onopen = () => {
      console.log('WebSocket connected');
      this.connectHandlers.forEach(handler => handler());
    };

    this.ws.onmessage = (event) => {
      console.log('Raw WebSocket message:', event.data);
      try {
        // Only try to parse if it looks like JSON
        if (typeof event.data === 'string' && (event.data.startsWith('{') || event.data.startsWith('['))) {
          const data = JSON.parse(event.data);
          this.messageHandlers.forEach(handler => handler(data));
        } else {
          // Ignore or handle non-JSON messages
          console.warn('Non-JSON WebSocket message:', event.data);
        }
      } catch (error) {
        console.error('Error parsing WebSocket message:', error, event.data);
      }
    };

    this.ws.onclose = () => {
      console.log('WebSocket disconnected');
      this.closeHandlers.forEach(handler => handler());
    };

    this.ws.onerror = (error) => {
      console.error('WebSocket error:', error);
      this.errorHandlers.forEach(handler => handler(error));
    };
  }

  disconnect() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  onMessage(handler: (data: any) => void) {
    this.messageHandlers.push(handler);
  }

  onError(handler: (error: any) => void) {
    this.errorHandlers.push(handler);
  }

  onConnect(handler: () => void) {
    this.connectHandlers.push(handler);
  }

  onClose(handler: () => void) {
    this.closeHandlers.push(handler);
  }

  send(data: any) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data));
    }
  }
}

export const websocketService = new WebSocketService(); 