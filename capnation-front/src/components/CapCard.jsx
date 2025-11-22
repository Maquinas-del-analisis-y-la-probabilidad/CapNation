import styles from "./CapCard.module.css";
import { styleMapper, sizeMapper } from "../cap-mapper";
import { Link } from "react-router-dom";

export function CapCard({
  brand,
  collaboration = "",
  size,
  style,
  price,
  imageUrl,
  id,
}) {
  const title = collaboration ? `${brand} - ${collaboration}` : brand;
  return (
    <article className={styles.capCard}>
      <img src={imageUrl} alt={`Gorra de la marca ${brand}`} />
      <span>{title}</span>
      <span>Talla: {sizeMapper[size]}</span>
      <span>Estilo: {styleMapper[style]}</span>
      <span>Precio: ${price}</span>
    </article>
  );
}
