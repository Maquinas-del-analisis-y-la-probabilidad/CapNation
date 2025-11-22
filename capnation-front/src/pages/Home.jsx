import { useEffect, useState } from "react";
import { CapCard } from "../components/CapCard.jsx";
import styles from "./Home.module.css";

const searchMapper = {
  brand: "brand",
  id: "id",
};

function useForm() {
  const BASE_URL = "http://localhost:8080/cap/find-all";
  const [serchType, setSearchType] = useState(searchMapper.id);
  const [caps, setCaps] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    fetch(BASE_URL)
      .then((res) => {
        setIsLoading(true);
        if (!res.ok) {
          throw new Error("Network response was not ok");
        }
        return res.json();
      })
      .then((data) => setCaps(data))
      .catch((error) => {
        console.error("Error fetching data:", error);
        setIsError(true);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  const handleSetSearchType = (type) => {
    setSearchType(searchMapper[type]);
  };

  return { handleSetSearchType, serchType, isLoading, caps, isError };
}

export function Home() {
  const { handleSetSearchType, serchType, isLoading, caps, isError } =
    useForm();

  if (isLoading) {
    return <p>Cargando...</p>;
  }

  if (isError) {
    return (
      <p>Error al cargar los datos. Por favor, inténtalo de nuevo más tarde.</p>
    );
  }

  return (
    <main>
      <header>
        <h1>Bienvenido a CapNation</h1>
        <p>Encuentra la s mejores gorras, de diferentes marcas y estilos</p>
      </header>
      <main className={styles.capsContainer}>
        <div className={styles.searchTypeButtons}>
          <button
            onClick={() => handleSetSearchType(searchMapper.id)}
            className={serchType === searchMapper.id ? styles.selected : ""}
          >
            Buscar por ID
          </button>
          <button
            onClick={() => handleSetSearchType(searchMapper.brand)}
            className={serchType === searchMapper.brand ? styles.selected : ""}
          >
            Buscar por Marca
          </button>
        </div>
        <form className={styles.serachForm} action="">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="1em"
            height="1em"
            viewBox="0 0 24 24"
          >
            <g
              fill="none"
              stroke="currentColor"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
            >
              <path d="m21 21l-4.34-4.34" />
              <circle cx="11" cy="11" r="8" />
            </g>
          </svg>
          <input
            type="text"
            name={serchType}
            placeholder={
              serchType === searchMapper.id
                ? "Busca gorras por ID"
                : "Busca gorras por marca"
            }
          />
          <button>Buscar</button>
        </form>

        <section>
          <h2>Resultados de la búsqueda</h2>

          <div className={styles.gridContainer}>
            {caps.map((cap) => {
              return <CapCard key={cap.id} {...cap} />;
            })}
          </div>
        </section>
      </main>
    </main>
  );
}
