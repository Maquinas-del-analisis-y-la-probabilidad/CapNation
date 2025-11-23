import { styleMapper, sizeMapper } from "../cap-mapper";
import styles from "./CapDialog.module.css";
import { Error } from "./Error";

export function CapDialog({ cap, isError, onCloseCap }) {
  if (isError) {
    return (
      <article className={styles.errorDialog}>
        <Error
          title="Error al cargar la gorra"
          description="La gorra que estabas buscando no se encuentra disponible."
        />
        <button onClick={onCloseCap}>Cerrar</button>
      </article>
    );
  }

  return (
    <>
      {cap && (
        <article className={styles.capDialog}>
          <img src={cap.imageUrl} alt={`Image of a ${cap.brand} cap`} />
          <div className={styles.details}>
            <span className={styles.description}>
              {cap.collaboration
                ? `Gorra ${cap.collaboration} marca ${cap.brand}`
                : `Gorra ${cap.brand}`}
            </span>
            <span>Talla: {sizeMapper[cap.size]}</span>
            <span>Estilo: {styleMapper[cap.style]}</span>
            <span className={styles.price}>Precio: {cap.price}</span>
          </div>
        </article>
      )}

      {cap && <button onClick={onCloseCap}>Cerrar</button>}
    </>
  );
}
