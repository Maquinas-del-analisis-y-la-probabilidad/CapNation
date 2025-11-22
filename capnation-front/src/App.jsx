import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router";
import { TopBar } from "./components/TopBar.jsx";
import { Footer } from "./components/Footer.jsx";
import { Home } from "./pages/Home.jsx";
function App() {
  return (
    <>
      <TopBar />
      <Home />

      <Footer />
    </>
  );
}

export default App;
