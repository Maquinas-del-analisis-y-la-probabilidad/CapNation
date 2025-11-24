import { Error } from "../components/Error";
import { Loading } from "../components/Loading";
import styles from "./SaveCap.module.css";
import { useState, useRef } from "react";

function useSaveCap() {
  const BASE_URL = "http://localhost:8080/cap";
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const onSaveCap = async (event) => {
    setIsLoading(true);

    const formData = new FormData(event.target);
    const cap = Object.fromEntries(formData.entries());
    console.log(cap);

    fetch(`${BASE_URL}/save`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(cap),
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error("Ya hay otra gorra con ese mismo id");
        }
        return res.json();
      })
      .catch((err) => {
        console.log(`Error: ${err.title}`);
        setError(err);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const clearError = () => {
    setError(null);
  };

  return {
    error,
    isLoading,
    onSaveCap,
    clearError,
  };
}

export function SaveCap() {
  const [image, setImage] = useState("");
  const refDialog = useRef();
  const { error, isLoading, onSaveCap, clearError } = useSaveCap();

  return (
    <main>
      <dialog ref={refDialog}>
        {isLoading && <Loading />}
        {!isLoading && error && (
          <Error
            title={"Error guardando la gorra"}
            description={
              error?.message || "Ya hay otra gorra con ese mismo id "
            }
          />
        )}
        {!isLoading && !error && (
          <article className={styles.success}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="128"
              height="128"
              viewBox="0 0 1024 1024"
            >
              <path
                fill="currentColor"
                d="M512 64a448 448 0 1 1 0 896a448 448 0 0 1 0-896m-55.808 536.384l-99.52-99.584a38.4 38.4 0 1 0-54.336 54.336l126.72 126.72a38.27 38.27 0 0 0 54.336 0l262.4-262.464a38.4 38.4 0 1 0-54.272-54.336z"
              />
            </svg>
            <p>La gorra se guardo correctamente</p>
          </article>
        )}

        <button
          onClick={() => {
            refDialog.current.close();
            clearError();
          }}
        >
          Cerrar
        </button>
      </dialog>

      <h1>Guardar una Gorra</h1>

      <form
        className={styles.capForm}
        onSubmit={(event) => {
          refDialog.current.showModal();
          event.preventDefault();
          onSaveCap(event);
        }}
      >
        <fieldset className={styles.capInfo}>
          <legend>Informacion de la Gorra</legend>

          <div>
            <label>ID de la gorra</label>
            <input type="number" placeholder="ID" name="id" required />
          </div>
          <div>
            <label>Marca de la gorra</label>
            <input type="text" placeholder="Marca" name="brand" required />
          </div>
          <div>
            <label>Estilo de la gorra</label>
            <select required name="style">
              <option value="">Selecciona un estilo</option>
              <option value="BASEBALL_CAP">Gorra Beisbolera</option>
              <option value="FLAT_CAP">Boina</option>
              <option value="SNAPBACK">Gorra Plana</option>
              <option value="TRUCKER_CAP">Gorra Camionera</option>
              <option value="DAD_CAP">Gorra Clasica</option>
              <option value="FITTED_CAP">Gorra Ajustada</option>
              <option value="BEANIE">Gorro</option>
              <option value="VISOR">Gorra Visor</option>
              <option value="FIVE_PANEL_CAP">Gorra de 5 Paneles</option>
            </select>
          </div>

          <div>
            <label>Color de la gorra</label>
            <input type="text" placeholder="Color" name="color" required />
          </div>
          <div>
            <label>Colaboracion </label>
            <input
              type="text"
              placeholder="Colaboracion"
              name="collaboration"
            />
          </div>
          <div>
            <label>Precio de la gorra</label>
            <input type="number" placeholder="Precio" name="price" required />
          </div>

          <div>
            <label>Talla</label>
            <select name="size">
              <option value="ONE_SIZE_FITS_ALL">Unica Talla</option>
              <option value="SMALL">S</option>
              <option value="MEDIUM">M</option>
              <option value="LARGE">L</option>
              <option value="EXTRA_LARGE">XL</option>
            </select>
          </div>

          <div>
            <label>Genero</label>
            <select name="gender">
              <option value="">Selecciona un genero</option>
              <option value="MALE">Masculino</option>
              <option value="FEMALE">Femenino</option>
            </select>
          </div>

          <div>
            <label>Unidades disponibles</label>
            <input
              type="number"
              placeholder="Unidades disponibles"
              name="stock"
              required
            />
          </div>
        </fieldset>

        <fieldset className={styles.imageFieldset}>
          <legend>Imagen</legend>
          <div>
            <div>
              <label>URL de la imagen</label>
              <input
                value={image}
                onChange={(event) => setImage(event.target.value)}
                type="text"
                placeholder="URL de la image"
                name="imageUrl"
                required
              />
            </div>
          </div>

          <div className={styles.imageContainer}>
            {image === "" ? (
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
                  strokeWidth="1.5"
                >
                  <path d="M6.5 8a2 2 0 1 0 4 0a2 2 0 0 0-4 0m14.427 1.99c-6.61-.908-12.31 4-11.927 10.51" />
                  <path d="M3 13.066c2.78-.385 5.275.958 6.624 3.1" />
                  <path d="M3 9.4c0-2.24 0-3.36.436-4.216a4 4 0 0 1 1.748-1.748C6.04 3 7.16 3 9.4 3h5.2c2.24 0 3.36 0 4.216.436a4 4 0 0 1 1.748 1.748C21 6.04 21 7.16 21 9.4v5.2c0 2.24 0 3.36-.436 4.216a4 4 0 0 1-1.748 1.748C17.96 21 16.84 21 14.6 21H9.4c-2.24 0-3.36 0-4.216-.436a4 4 0 0 1-1.748-1.748C3 17.96 3 16.84 3 14.6z" />
                </g>
              </svg>
            ) : (
              <img src={image} alt="Image of the cap to save" />
            )}
          </div>
        </fieldset>

        <button>Guardar</button>
      </form>
    </main>
  );
}
