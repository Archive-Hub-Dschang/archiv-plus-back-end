import pandas as pd
from scipy.stats import chi2

def anova_one_way_non_parametrique(data=None, alpha=0.005):
    """
    data : dict, ex: {'A': [5, 4, 4, 5, 6], 'B': [9, 8, 8, 6, 9], ...}
    alpha : float, niveau de signification
    Retourne un résumé texte du test de Kruskal-Wallis.
    """
    if data is None:
        data = {
            'A': [5, 4, 4, 5, 6],
            'B': [9, 8, 8, 6, 9],
            'C': [3, 5, 2, 3, 7],
            'D': [2, 3, 4, 1, 4],
            'E': [7, 6, 9, 4, 7]
        }

    # 1. Mise sous forme longue
    df = pd.DataFrame(data)
    df_long = df.melt(var_name='Groupe', value_name='Valeur')

    # 2. Attribuer les rangs (en tenant compte des ex æquo)
    df_long['Rang'] = df_long['Valeur'].rank(method='average')

    # 3. Calcul des sommes de rangs par groupe
    rank_sums = df_long.groupby('Groupe')['Rang'].sum()

    # 4. Calcul manuel de la statistique H
    n_total = len(df_long)
    k = df_long['Groupe'].nunique()
    H_num = 12 / (n_total * (n_total + 1))

    total = 0
    for group in rank_sums.index:
        rank_sum_squared = rank_sums[group] ** 2
        n = df_long[df_long['Groupe'] == group].shape[0]
        total += rank_sum_squared / n

    H = H_num * total
    H -= 3 * (n_total + 1)

    # 5. Valeur critique et p-value
    df_deg = k - 1
    chi2_critical = chi2.ppf(1 - alpha, df_deg)
    p_value = 1 - chi2.cdf(H, df_deg)

    # 6. Résultat (texte)
    result = []
    result.append("=== Test de Kruskal-Wallis (manuel avec pandas) ===")
    result.append(f"H = {H:.4f}")
    result.append(f"Chi² critique (alpha={alpha}) = {chi2_critical:.4f}")
    result.append(f"p-value = {p_value:.6f}")
    if H > chi2_critical:
        result.append("H₀ est rejetée : Il existe au moins une différence significative.")
    else:
        result.append("H₀ n’est pas rejetée : Pas de différence significative détectée.")
    result.append("\nSommes des rangs par groupe :")
    result.append(str(rank_sums))
    return "\n".join(result)