import { create } from "zustand";

interface AppState {
    isSignedIn: boolean;
    cartCount: number;
    toggleSignIn: () => void;
    updateCartCount: (count: number) => void;
}

const useStore = create<AppState>((set) => ({
    isSignedIn: false,
    cartCount: 0,
    toggleSignIn: () => set((state) => ({ isSignedIn: !state.isSignedIn })),
    updateCartCount: (count) => set(() => ({ cartCount: count })),
}));

export default useStore;