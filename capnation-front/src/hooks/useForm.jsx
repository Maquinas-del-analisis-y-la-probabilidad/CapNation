import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { searchMapper } from "../pages/Home.jsx";

export function useForm() {
  const BASE_URL = "http://localhost:8080/cap";
  const [serchType, setSearchType] = useState(searchMapper.id);
  const [caps, setCaps] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isError, setIsError] = useState(false);
  const [searchParams, setSearchParams] = useSearchParams();
  const [cap, setCap] = useState(null);
  const [capError, setCapError] = useState(null);
  const [brandError, setBrandError] = useState(null);
  const [brandValue, setBrandValue] = useState(() => {
    const brand = searchParams.get("brand");
    if (brand) {
      return brand;
    }
  });

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

  // fetch by brand
  useEffect(() => {
    const brand = searchParams.get("brand");
    if (brand) {
      const params = new URLSearchParams();
      params.append("brand", brand);

      fetch(`${BASE_URL}/find/brand?${params}`)
        .then((res) => {
          setIsLoading(true);
          if (!res.ok) {
            throw new Error(`No existe ninguna gorra con la marca ${brand}`);
          }
          return res.json();
        })
        .then((data) => setCaps(data))
        .catch((error) => {
          console.log(error);
          setBrandError(error.message);
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  }, [searchParams]);

  // fetch by id
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

  const setBrand = (brand) => {
    const newParams = new URLSearchParams();
    newParams.append("brand", brand);
    setSearchParams(newParams);
  };

  const onClean = () => {
    const voidParams = new URLSearchParams();
    setSearchParams(voidParams);
    setBrandError(null);
    fetchAllCaps();
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
    setBrand,
    brandError,
    brandValue,
    onClean,
  };
}
