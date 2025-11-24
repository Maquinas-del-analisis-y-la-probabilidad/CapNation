import "./App.css";
import { Routes, Route } from "react-router";
import { TopBar } from "./components/TopBar.jsx";
import { Footer } from "./components/Footer.jsx";
import { Home } from "./pages/Home.jsx";
import { SaveCap } from "./pages/SaveCap.jsx";
function App() {
  return (
    <>
      <TopBar />
      <Routes>
        <Route path="/save-cap" element={<SaveCap />}></Route>
        <Route path="/" element={<Home />}></Route>
      </Routes>
      <Footer />
    </>
  );
}

export default App;
