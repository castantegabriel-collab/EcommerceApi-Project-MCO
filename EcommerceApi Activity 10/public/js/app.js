/**
 * ECommerceApi Frontend Application
 *
 * Handles dynamic product fetching from the backend API using the Fetch API,
 * with comprehensive error handling and responsive rendering.
 */

// Configuration
const API_BASE_URL =
  window.__BACKEND_URL__ || ""
    ? `${window.__BACKEND_URL__.replace(/\/$/, "")}/api/v1/products`
    : "/api/v1/products";

// DOM Elements
const productContainer = document.getElementById("productContainer");
const loadingIndicator = document.getElementById("loadingIndicator");
const errorContainer = document.getElementById("errorContainer");
const emptyState = document.getElementById("emptyState");
const categoryFilter = document.getElementById("categoryFilter");
const priceFilter = document.getElementById("priceFilter");
const applyFiltersBtn = document.getElementById("applyFiltersBtn");
const resetFiltersBtn = document.getElementById("resetFiltersBtn");

// Application State
let allProducts = [];

/**
 * Initializes the application when the DOM is fully loaded.
 * Sets up event listeners and fetches initial product data.
 */
document.addEventListener("DOMContentLoaded", () => {
  console.log("Application initialized");
  setupEventListeners();
  fetchAndRenderProducts();
});

/**
 * Sets up event listeners for filter controls.
 */
function setupEventListeners() {
  applyFiltersBtn.addEventListener("click", handleApplyFilters);
  resetFiltersBtn.addEventListener("click", handleResetFilters);
}

/**
 * Handles the apply filters button click.
 * Filters products based on selected category and max price.
 */
function handleApplyFilters() {
  const category = categoryFilter.value;
  const maxPrice = priceFilter.value;

  let filtered = allProducts;

  if (category) {
    filtered = filtered.filter(
      (product) => product.category && product.category.name === category,
    );
    console.log(
      `Filtered by category: ${category}, ${filtered.length} products found`,
    );
  }

  if (maxPrice) {
    const price = parseFloat(maxPrice);
    if (!isNaN(price)) {
      filtered = filtered.filter((product) => product.price <= price);
      console.log(
        `Filtered by price <= ${price}, ${filtered.length} products found`,
      );
    }
  }

  renderProducts(filtered);
}

/**
 * Handles the reset filters button click.
 * Clears all filter selections and displays all products.
 */
function handleResetFilters() {
  categoryFilter.value = "";
  priceFilter.value = "";
  renderProducts(allProducts);
  console.log("Filters reset. Displaying all products.");
}

/**
 * Fetches products from the backend API using the Fetch API.
 * Implements comprehensive error handling with try...catch.
 * Handles authentication errors (401/403) by redirecting to login.
 *
 * @returns {Promise<Array>} Array of product objects from the API
 * @throws {Error} If the API request fails or returns an error status
 */
async function fetchProducts() {
  try {
    console.log("Fetching products from API...");

    const response = await fetch(API_BASE_URL, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      if (response.status === 401) {
        console.warn(
          "Unauthorized (401): User session expired or not logged in",
        );
        handleAuthenticationError();
        throw new Error("Your session has expired. Please log in again.");
      } else if (response.status === 403) {
        console.warn("Forbidden (403): User lacks required permissions");
        handleForbiddenError();
        throw new Error("You do not have permission to access this resource.");
      } else if (response.status === 404) {
        throw new Error(
          "Products endpoint not found (404). Please check the API configuration.",
        );
      } else if (response.status === 500) {
        throw new Error("Backend server error (500). Please try again later.");
      } else {
        throw new Error(
          `API returned status ${response.status}: ${response.statusText}`,
        );
      }
    }

    const data = await response.json();
    console.log("Successfully fetched products:", data);
    return data;
  } catch (error) {
    if (error instanceof TypeError) {
      console.error(
        "Network error - unable to reach the backend API:",
        error.message,
      );
      throw new Error(
        "Network error: Cannot reach the backend server at " + API_BASE_URL,
      );
    } else if (error instanceof SyntaxError) {
      console.error("Invalid JSON response from the API:", error.message);
      throw new Error("Invalid response format from the server.");
    } else {
      console.error("Fetch error:", error.message);
      throw error;
    }
  }
}

/**
 * Fetches products and renders them to the DOM.
 * Manages loading state, error handling, and empty state display.
 */
async function fetchAndRenderProducts() {
  try {
    showLoading(true);
    hideError();

    allProducts = await fetchProducts();
    renderProducts(allProducts);
  } catch (error) {
    console.error("Error in fetchAndRenderProducts:", error.message);
    showError(error.message);
    renderProducts([]);
  } finally {
    showLoading(false);
  }
}

/**
 * Renders products to the DOM.
 * Displays products in a grid layout or shows empty state if no products.
 *
 * @param {Array} products - Array of product objects to render
 */
function renderProducts(products) {
  productContainer.innerHTML = "";

  if (!products || products.length === 0) {
    showEmptyState(true);
    console.log("No products to display");
    return;
  }

  showEmptyState(false);

  products.forEach((product) => {
    const productCard = createProductCard(product);
    productContainer.appendChild(productCard);
  });

  console.log(`Rendered ${products.length} products to the DOM`);
}

/**
 * Creates a product card element for a single product.
 * Includes product image, name, description, price, category, and stock status.
 *
 * @param {Object} product - The product object
 * @returns {HTMLElement} The product card element
 */
function createProductCard(product) {
  const card = document.createElement("div");
  card.className = "product-card";

  const imageUrl =
    product.imageUrl || "https://via.placeholder.com/250x200?text=No+Image";
  const categoryName =
    product.category && product.category.name
      ? product.category.name
      : "Uncategorized";
  const stockStatus = product.stockQuantity > 0 ? "In Stock" : "Out of Stock";
  const stockClass = product.stockQuantity > 0 ? "in-stock" : "out-of-stock";

  card.innerHTML = `
        <img src="${escapeHtml(imageUrl)}" alt="${escapeHtml(product.name)}" class="product-image">
        <div class="product-details">
            <h3 class="product-name">${escapeHtml(product.name)}</h3>
            <p class="product-description">${escapeHtml(product.description || "No description available")}</p>
            <div class="product-meta">
                <span class="product-category">${escapeHtml(categoryName)}</span>
                <span class="product-stock ${stockClass}">${stockStatus}</span>
            </div>
            <div class="product-price">$${formatPrice(product.price)}</div>
            <button class="btn-add-to-cart" onclick="addToCart(${product.id}, '${escapeHtml(product.name)}')" 
                    ${product.stockQuantity === 0 ? "disabled" : ""}>
                Add to Cart
            </button>
        </div>
    `;

  return card;
}

/**
 * Placeholder function for adding a product to the cart.
 * In a full implementation, this would manage a shopping cart.
 *
 * @param {number} productId - The ID of the product to add
 * @param {string} productName - The name of the product
 */
function addToCart(productId, productName) {
  console.log(`Added to cart: Product ${productId} - ${productName}`);
  alert(`"${productName}" has been added to your cart!`);
}

/**
 * Shows or hides the loading indicator.
 *
 * @param {boolean} show - Whether to show (true) or hide (false) the indicator
 */
function showLoading(show) {
  if (show) {
    loadingIndicator.classList.remove("hidden");
  } else {
    loadingIndicator.classList.add("hidden");
  }
}

/**
 * Displays an error message to the user.
 *
 * @param {string} message - The error message to display
 */
function showError(message) {
  errorContainer.textContent = "Error: " + message;
  errorContainer.classList.remove("hidden");
  console.error("Error displayed to user:", message);
}

/**
 * Hides the error message container.
 */
function hideError() {
  errorContainer.classList.add("hidden");
}

/**
 * Shows or hides the empty state message.
 *
 * @param {boolean} show - Whether to show (true) or hide (false) the empty state
 */
function showEmptyState(show) {
  if (show) {
    emptyState.classList.remove("hidden");
  } else {
    emptyState.classList.add("hidden");
  }
}

/**
 * Formats a price number to a string with 2 decimal places.
 *
 * @param {number} price - The price to format
 * @returns {string} Formatted price string (e.g., "19.99")
 */
function formatPrice(price) {
  return parseFloat(price).toFixed(2);
}

/**
 * Escapes HTML special characters to prevent XSS attacks.
 * Converts HTML entities to their encoded equivalents.
 *
 * @param {string} text - The text to escape
 * @returns {string} The escaped text safe for HTML insertion
 */
function escapeHtml(text) {
  if (!text) return "";
  const map = {
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#039;",
  };
  return text.replace(/[&<>"']/g, (m) => map[m]);
}

console.log("ECommerceApi Frontend Application Loaded");
console.log("API Endpoint:", API_BASE_URL);

/**
 * Handles authentication errors (401 Unauthorized).
 * Redirects user to login page.
 */
function handleAuthenticationError() {
  console.log("Redirecting to login page...");
  window.location.href = "/login.html";
}

/**
 * Handles authorization errors (403 Forbidden).
 * Shows an access denied message and optionally redirects.
 */
function handleForbiddenError() {
  alert("Access Denied: You do not have permission to access this resource.");
  window.location.href = "/index.html";
}

/**
 * Logs out the current user by invalidating the session.
 * Redirects to login page after successful logout.
 */
async function logout() {
  try {
    const response = await fetch("/api/v1/auth/logout", {
      method: "POST",
      credentials: "include",
    });

    if (response.ok) {
      console.log("Logout successful");
      window.location.href = "/login.html";
    } else {
      console.error("Logout failed");
      window.location.href = "/login.html";
    }
  } catch (error) {
    console.error("Logout error:", error);
    window.location.href = "/login.html";
  }
}

/**
 * Fetches the current user information to check authentication status.
 * Can be used to verify if user is logged in.
 */
async function checkAuthenticationStatus() {
  try {
    const response = await fetch("/api/v1/products", {
      method: "GET",
      credentials: "include",
    });

    if (response.status === 401) {
      return false;
    }
    return true;
  } catch (error) {
    console.error("Error checking authentication status:", error);
    return false;
  }
}
