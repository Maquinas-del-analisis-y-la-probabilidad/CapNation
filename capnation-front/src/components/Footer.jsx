import styles from "./Footer.module.css";

export function Footer() {
  return (
    <footer className={styles.footerBar}>
      <small>
        &copy; {new Date().getFullYear()} CapNation. Todos los derechos
        reservados.
      </small>
    </footer>
  );
}
