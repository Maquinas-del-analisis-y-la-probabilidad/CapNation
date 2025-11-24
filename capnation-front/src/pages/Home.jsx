import { useRef } from "react";
import { CapCard } from "../components/CapCard.jsx";
import styles from "./Home.module.css";
import { useForm } from "../hooks/useForm.jsx";
import { CapDialog } from "../components/CapDialog.jsx";
import { Loading } from "../components/Loading.jsx";
import { Error } from "../components/Error.jsx";

export const searchMapper = {
  brand: "brand",
  id: "id",
};

export function Home() {
  const refDialog = useRef();
  const refInput = useRef();

  const {
    handleSetSearchType,
    serchType: searchType,
    isLoading,
    caps,
    isError,
    cap,
    onCloseCap,
    capError,
    setID,
    setBrand,
    brandError,
    brandValue,
    onClean,
  } = useForm();

  if (isLoading) {
    return <Loading />;
  }

  if (isError) {
    return (
      <Error
        title="Error al cargar los datos"
        description="Por favor, inténtalo de nuevo más tarde"
      />
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
            refInput.current.value = "";
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
            onClick={() => {
              handleSetSearchType(searchMapper.id);
              refInput.current.value = "";
            }}
            className={searchType === searchMapper.id ? styles.selected : ""}
          >
            Buscar por ID
          </button>
          <button
            onClick={() => {
              handleSetSearchType(searchMapper.brand);
              refInput.current.value = "";
            }}
            className={searchType === searchMapper.brand ? styles.selected : ""}
          >
            Buscar por Marca
          </button>
        </div>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            if (searchType === searchMapper.id) {
              const data = new FormData(event.target);
              setID(data.get("id"));
              refDialog.current.showModal();
            }
            if (searchType === searchMapper.brand) {
              const data = new FormData(event.target);
              setBrand(data.get("brand"));
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
            ref={refInput}
            type="text"
            name={searchType}
            defaultValue={brandValue}
            placeholder={
              searchType === searchMapper.id
                ? "Busca gorras por ID"
                : "Busca gorras por marca"
            }
          />
          <button>Buscar</button>
        </form>
        {searchType === searchMapper.brand ? (
          <button
            style={{ width: "fit-content", alignSelf: "center" }}
            onClick={onClean}
          >
            Limpiar
          </button>
        ) : (
          <></>
        )}

        <section>
          {brandError === null ? (
            <CapsGrid caps={caps} />
          ) : (
            <Error
              title="Error al cargar las gorras"
              description={brandError}
            />
          )}
        </section>
      </main>
    </main>
  );
}

function CapsGrid({ caps }) {
  return (
    <>
      <h2>Resultados de la búsqueda</h2>
      <div className={styles.gridContainer}>
        {caps.map((cap) => {
          return <CapCard key={cap.id} {...cap} />;
        })}
      </div>
    </>
  );
}
