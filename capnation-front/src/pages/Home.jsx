import { useEffect, useRef, useState } from "react";
import { CapCard } from "../components/CapCard.jsx";
import styles from "./Home.module.css";
import { useSearchParams } from "react-router-dom";
import { CapDialog } from "../components/CapDialog.jsx";

const searchMapper = {
  brand: "brand",
  id: "id",
};

function useForm() {
  const BASE_URL = "http://localhost:8080/cap";
  const [serchType, setSearchType] = useState(searchMapper.id);
  const [caps, setCaps] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isError, setIsError] = useState(false);
  const [searchParams, setSearchParams] = useSearchParams();
  const [cap, setCap] = useState(null);
  const [capError, setCapError] = useState(null);

  const fetchAllCaps = () => {
    fetch(`${BASE_URL}/find-all`)
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
  };

  useEffect(() => {
    const brand = searchParams.get("brand");
    if (brand) {
      const params = new URLSearchParams();
      params.append("brand", brand);

      fetch(`${BASE_URL}/find/brand?${params}`)
        .then((res) => {
          setIsLoading(true);
          if (!res.ok) {
            throw new Error("Failed to fetch caps by brand");
          }
          return res.json();
        })
        .then((data) => setCaps(data))
        .catch((error) => {
          console.log(error);
          setIsError(true);
        })
        .finally(() => setIsLoading(false));
    }
  }, [searchParams]);

  useEffect(() => {
    const id = searchParams.get("id");

    if (id) {
      const params = new URLSearchParams();
      params.append("id", id);
      fetch(`${BASE_URL}/find?${params}`)
        .then((res) => {
          if (!res.ok) {
            throw new Error("failed to fetch by id");
          }
          return res.json();
        })
        .then((data) => setCap(data))
        .catch((error) => {
          console.log(error);
          setCapError(true);
        });
    }
  }, [searchParams]);

  useEffect(() => {
    fetchAllCaps();
  }, []);

  const handleSetSearchType = (type) => {
    setSearchType(searchMapper[type]);
  };

  const onCloseCap = () => {
    setCapError(null);
    setCap(null);

    const voidSearchParams = new URLSearchParams();
    setSearchParams(voidSearchParams);
  };

  const setID = (id) => {
    const newParams = new URLSearchParams();

    newParams.append("id", id);
    setSearchParams(newParams);
  };

  return {
    handleSetSearchType,
    serchType,
    isLoading,
    caps,
    isError,
    cap,
    onCloseCap,
    capError,
    setID,
  };
}

export function Home() {
  const refDialog = useRef();
  const {
    handleSetSearchType,
    serchType,
    isLoading,
    caps,
    isError,
    cap,
    onCloseCap,
    capError,
    setID,
  } = useForm();

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
      <dialog ref={refDialog}>
        <CapDialog
          cap={cap}
          isError={capError}
          onCloseCap={() => {
            refDialog.current.close();
            onCloseCap();
          }}
        />
      </dialog>

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
        <form
          onSubmit={(event) => {
            event.preventDefault();
            if (serchType === searchMapper.id) {
              const data = new FormData(event.target);
              setID(data.get("id"));
              refDialog.current.showModal();
            }
          }}
          className={styles.serachForm}
          action=""
        >
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
